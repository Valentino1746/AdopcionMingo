package mx.edu.unpa.miandroid

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.ActualizarPerfilRequest
import mx.edu.unpa.miandroid.model.PerfilResponse
import mx.edu.unpa.miandroid.model.UploadFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PerfilActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var urlFotoSubida: String? = null

    private lateinit var imgPerfil: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etApellidoPaterno: EditText
    private lateinit var etApellidoMaterno: EditText
    private lateinit var etTelefono: EditText
    private lateinit var txtEmail: TextView
    private lateinit var progressFoto: ProgressBar

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imgPerfil.setImageURI(it)
            subirFotoPerfil(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        imgPerfil        = findViewById(R.id.imgPerfil)
        etNombre         = findViewById(R.id.etNombre)
        etApellidoPaterno= findViewById(R.id.etApellidoPaterno)
        etApellidoMaterno= findViewById(R.id.etApellidoMaterno)
        etTelefono       = findViewById(R.id.etTelefono)
        txtEmail         = findViewById(R.id.txtEmail)
        progressFoto     = findViewById(R.id.progressFoto)

        imgPerfil.setOnClickListener { galleryLauncher.launch("image/*") }

        findViewById<MaterialButton>(R.id.btnGuardar).setOnClickListener { guardarPerfil() }
        findViewById<MaterialButton>(R.id.btnRegresar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarPerfil()
    }

    private fun cargarPerfil() {
        val idUsuario = prefs.getInt("idUsuario", -1)
        if (idUsuario == -1) { finish(); return }

        RetrofitClient.instance.obtenerPerfil(idUsuario)
            .enqueue(object : Callback<PerfilResponse> {
                override fun onResponse(call: Call<PerfilResponse>, response: Response<PerfilResponse>) {
                    if (response.isSuccessful) {
                        val perfil = response.body() ?: return
                        etNombre.setText(perfil.nombre)
                        etApellidoPaterno.setText(perfil.apellidoPaterno)
                        etApellidoMaterno.setText(perfil.apellidoMaterno ?: "")
                        etTelefono.setText(perfil.telefono ?: "")
                        txtEmail.text = perfil.email
                        urlFotoSubida = perfil.urlFotoPerfil

                        if (!perfil.urlFotoPerfil.isNullOrBlank()) {
                            val urlCompleta = RetrofitClient.BASE_URL.trimEnd('/') +
                                    perfil.urlFotoPerfil
                            Glide.with(this@PerfilActivity)
                                .load(urlCompleta)
                                .circleCrop()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(imgPerfil)
                        }
                    }
                }
                override fun onFailure(call: Call<PerfilResponse>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity,
                        "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun guardarPerfil() {
        val nombre = etNombre.text.toString().trim()
        val apPat  = etApellidoPaterno.text.toString().trim()

        if (nombre.isEmpty() || apPat.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido paterno son obligatorios",
                Toast.LENGTH_SHORT).show()
            return
        }

        val idUsuario = prefs.getInt("idUsuario", -1)
        val request = ActualizarPerfilRequest(
            nombre           = nombre,
            apellidoPaterno  = apPat,
            apellidoMaterno  = etApellidoMaterno.text.toString().trim().ifBlank { null },
            telefono         = etTelefono.text.toString().trim().ifBlank { null },
            urlFotoPerfil    = urlFotoSubida
        )

        RetrofitClient.instance.actualizarPerfil(idUsuario, request)
            .enqueue(object : Callback<PerfilResponse> {
                override fun onResponse(call: Call<PerfilResponse>, response: Response<PerfilResponse>) {
                    if (response.isSuccessful) {
                        // Actualizar nombre en prefs para el saludo
                        prefs.edit().putString("nombre", response.body()?.nombre).apply()
                        Toast.makeText(this@PerfilActivity,
                            "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@PerfilActivity,
                            "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<PerfilResponse>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity,
                        "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun subirFotoPerfil(uri: Uri) {
        progressFoto.visibility = View.VISIBLE
        val file = File(cacheDir, "perfil_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }

        val body = MultipartBody.Part.createFormData(
            "file", file.name,
            file.asRequestBody("image/*".toMediaType())
        )

        RetrofitClient.instance.uploadImage(body)
            .enqueue(object : Callback<UploadFile> {
                override fun onResponse(call: Call<UploadFile>, response: Response<UploadFile>) {
                    progressFoto.visibility = View.GONE
                    if (response.isSuccessful) {
                        urlFotoSubida = response.body()?.ruta
                        Toast.makeText(this@PerfilActivity,
                            "Foto lista", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UploadFile>, t: Throwable) {
                    progressFoto.visibility = View.GONE
                    Toast.makeText(this@PerfilActivity,
                        "Error al subir foto", Toast.LENGTH_SHORT).show()
                }
            })
    }
}