package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CrearMascotaRequest
import mx.edu.unpa.miandroid.model.Mascota
import mx.edu.unpa.miandroid.model.TipoMascota
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarMascotaActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var tiposMascota: List<TipoMascota> = emptyList()
    private var idTipoSeleccionado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_mascota)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        cargarTipos()

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener { registrar() }
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
                        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                        val spinner = findViewById<Spinner>(R.id.spinnerTipo)
                        spinner.adapter = spinnerAdapter
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
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

        // Sexo viene de RadioGroup
        val radioSexo = findViewById<RadioGroup>(R.id.radioGroupSexo)
        val sexo = when (radioSexo.checkedRadioButtonId) {
            R.id.radioMacho  -> "Macho"
            R.id.radioHembra -> "Hembra"
            else             -> ""
        }

        // Edge cases
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

        val idDonador = prefs.getInt("idUsuario", -1)
        if (idDonador == -1) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val request = CrearMascotaRequest(
            nombre          = nombre,
            raza            = raza,
            sexo            = sexo,
            edadAproximada  = "",
            descripcion     = ""
        )

        RetrofitClient.instance.crearMascota(idDonador, idTipoSeleccionado, request)
            .enqueue(object : Callback<Mascota> {
                override fun onResponse(call: Call<Mascota>, response: Response<Mascota>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegistrarMascotaActivity,
                            "Mascota registrada", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistrarMascotaActivity, CategoriasActivity::class.java))
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