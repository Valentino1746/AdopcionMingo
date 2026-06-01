package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CrearMascotaRequest
import mx.edu.unpa.miandroid.model.Mascota
import mx.edu.unpa.miandroid.model.TipoMascota
import mx.edu.unpa.miandroid.model.UploadFile
import mx.edu.unpa.miapp.permissions.PermissionManager
import mx.edu.unpa.miapp.permissions.PermissionState
import mx.edu.unpa.miapp.permissions.PermissionType
import mx.edu.unpa.miapp.permissions.PermissionUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RegistrarMascotaActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var permissionManager: PermissionManager

    private var tiposMascota: List<TipoMascota> = emptyList()
    private var idTipoSeleccionado: Int = -1
    private var urlFotoSubida: String? = null

    private lateinit var imgPreview: ImageView
    private lateinit var btnTomarFoto: com.google.android.material.button.MaterialButton
    private lateinit var btnElegirGaleria: com.google.android.material.button.MaterialButton
    private lateinit var progressSubida: ProgressBar
    private lateinit var txtEstadoFoto: TextView

    // Archivo temporal donde la cámara escribirá la foto completa
    private var archivoFotoTemporal: File? = null

    // ── CAMBIO PRINCIPAL ──────────────────────────────────────────────────────
    // TakePicture escribe la foto completa en el Uri que le pasamos
    // y devuelve true/false indicando si el usuario tomó la foto o canceló
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exitosoCaptura ->
        if (exitosoCaptura && archivoFotoTemporal != null) {
            // Mostrar preview desde el archivo (no desde bitmap de baja res)
            imgPreview.setImageURI(Uri.fromFile(archivoFotoTemporal))
            imgPreview.visibility = View.VISIBLE
            // Subir el archivo directamente — sin conversión de Bitmap
            subirArchivo(archivoFotoTemporal!!)
        } else {
            // Edge case: usuario canceló o el archivo quedó vacío
            archivoFotoTemporal?.delete()
            archivoFotoTemporal = null
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    // Galería — igual que antes, sin cambios
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imgPreview.setImageURI(uri)
            imgPreview.visibility = View.VISIBLE
            subirDesdeUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_mascota)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)
        permissionManager = PermissionManager(this)

        imgPreview       = findViewById(R.id.imgPreviewMascota)
        btnTomarFoto     = findViewById(R.id.btnTomarFoto)
        btnElegirGaleria = findViewById(R.id.btnElegirGaleria)
        progressSubida   = findViewById(R.id.progressSubida)
        txtEstadoFoto    = findViewById(R.id.txtEstadoFoto)

        btnTomarFoto.setOnClickListener { solicitarCamara() }
        btnElegirGaleria.setOnClickListener { galleryLauncher.launch("image/*") }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegistrar)
            .setOnClickListener { registrar() }

        cargarTipos()
    }

    private fun solicitarCamara() {
        permissionManager.requestPermission(PermissionType.CAMERA) { state ->
            when (state) {
                PermissionState.GRANTED -> abrirCamara()
                PermissionState.DENIED ->
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                PermissionState.PERMANENTLY_DENIED -> {
                    Toast.makeText(this,
                        "Habilita el permiso en configuración", Toast.LENGTH_LONG).show()
                    PermissionUtils.openAppSettings(this)
                }
            }
        }
    }

    // ── CAMBIO PRINCIPAL ──────────────────────────────────────────────────────
    private fun abrirCamara() {
        // 1. Crea el archivo temporal ANTES de abrir la cámara
        archivoFotoTemporal = File(cacheDir, "mascota_${System.currentTimeMillis()}.jpg")

        // 2. Convierte el File a un Uri seguro usando FileProvider
        //    Sin FileProvider, Android 7+ lanza FileUriExposedException
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",   // debe coincidir con el authority del Manifest
            archivoFotoTemporal!!
        )

        // 3. Lanza la cámara pasándole el Uri donde debe guardar la foto
        cameraLauncher.launch(uri)
    }
    // ─────────────────────────────────────────────────────────────────────────

    private fun subirDesdeUri(uri: Uri) {
        val file = File(cacheDir, "mascota_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        subirArchivo(file)
    }

    private fun subirArchivo(file: File) {
        progressSubida.visibility = View.VISIBLE
        txtEstadoFoto.text = "Subiendo imagen..."
        txtEstadoFoto.visibility = View.VISIBLE
        urlFotoSubida = null

        val requestFile = file.asRequestBody("image/*".toMediaType())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.instance.uploadImage(body)
            .enqueue(object : Callback<UploadFile> {
                override fun onResponse(
                    call: Call<UploadFile>,
                    response: Response<UploadFile>
                ) {
                    progressSubida.visibility = View.GONE
                    if (response.isSuccessful) {
                        urlFotoSubida = response.body()?.ruta
                        txtEstadoFoto.text = "✓ Foto lista"
                        txtEstadoFoto.setTextColor(getColor(R.color.color_disponible))
                    } else {
                        txtEstadoFoto.text = "Error al subir foto (${response.code()})"
                        txtEstadoFoto.setTextColor(getColor(R.color.color_no_disponible))
                    }
                }

                override fun onFailure(call: Call<UploadFile>, t: Throwable) {
                    progressSubida.visibility = View.GONE
                    txtEstadoFoto.text = "Sin conexión al subir foto"
                    txtEstadoFoto.setTextColor(getColor(R.color.color_no_disponible))
                }
            })
    }

    private fun cargarTipos() {
        RetrofitClient.instance.getTiposMascota()
            .enqueue(object : Callback<List<TipoMascota>> {
                override fun onResponse(
                    call: Call<List<TipoMascota>>,
                    response: Response<List<TipoMascota>>
                ) {
                    if (response.isSuccessful) {
                        tiposMascota = response.body() ?: emptyList()
                        val nombres = tiposMascota.map { it.descripcion }
                        val spinnerAdapter = ArrayAdapter(
                            this@RegistrarMascotaActivity,
                            android.R.layout.simple_spinner_item,
                            nombres
                        ).also {
                            it.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                            )
                        }
                        val spinner = findViewById<Spinner>(R.id.spinnerTipo)
                        spinner.adapter = spinnerAdapter
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View?,
                                    pos: Int,
                                    id: Long
                                ) {
                                    idTipoSeleccionado = tiposMascota[pos].idTipoMascota
                                }
                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }
                    }
                }
                override fun onFailure(call: Call<List<TipoMascota>>, t: Throwable) {
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Error al cargar tipos", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registrar() {
        val nombre = findViewById<EditText>(R.id.etNombre).text.toString().trim()
        val raza   = findViewById<EditText>(R.id.etRaza).text.toString().trim()
        val radioSexo = findViewById<RadioGroup>(R.id.radioGroupSexo)
        val sexo = when (radioSexo.checkedRadioButtonId) {
            R.id.radioMacho  -> "Macho"
            R.id.radioHembra -> "Hembra"
            else             -> ""
        }

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }
        if (sexo.isEmpty()) {
            Toast.makeText(this, "Selecciona el sexo", Toast.LENGTH_SHORT).show()
            return
        }
        if (idTipoSeleccionado == -1) {
            Toast.makeText(this, "Selecciona el tipo de mascota", Toast.LENGTH_SHORT).show()
            return
        }
        if (progressSubida.visibility == View.VISIBLE) {
            Toast.makeText(this, "Espera a que termine de subir la foto",
                Toast.LENGTH_SHORT).show()
            return
        }

        val idDonador = prefs.getInt("idUsuario", -1)
        if (idDonador == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        RetrofitClient.instance.crearMascota(
            idDonador,
            idTipoSeleccionado,
            CrearMascotaRequest(
                nombre         = nombre,
                raza           = raza,
                sexo           = sexo,
                edadAproximada = "",
                descripcion    = "",
                urlFoto        = urlFotoSubida
            )
        ).enqueue(object : Callback<Mascota> {
            override fun onResponse(call: Call<Mascota>, response: Response<Mascota>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Mascota registrada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegistrarMascotaActivity,
                        CategoriasActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegistrarMascotaActivity,
                        "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Mascota>, t: Throwable) {
                Toast.makeText(this@RegistrarMascotaActivity,
                    "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}