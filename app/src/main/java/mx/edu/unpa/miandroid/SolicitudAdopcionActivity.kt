package mx.edu.unpa.miandroid

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CrearSolicitudRequest
import mx.edu.unpa.miandroid.model.SolicitudAdopcion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitudAdopcionActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud_adopcion)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        val idMascota    = intent.getIntExtra("idMascota", -1)
        val nombreMascota= intent.getStringExtra("nombreMascota") ?: "la mascota"

        findViewById<TextView>(R.id.txtNombreMascotaSolicitud).text =
            "Solicitud para adoptar a $nombreMascota"

        findViewById<MaterialButton>(R.id.btnEnviarSolicitud).setOnClickListener {
            enviarSolicitud(idMascota)
        }
        findViewById<MaterialButton>(R.id.btnCancelar).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun enviarSolicitud(idMascota: Int) {
        val telefono       = findViewById<EditText>(R.id.etTelefonoContacto).text.toString().trim()
        val vivienda       = findViewById<Spinner>(R.id.spinnerTipoVivienda).selectedItem.toString()
        val otrasMascotas  = findViewById<CheckBox>(R.id.cbTieneOtrasMascotas).isChecked
        val ninos          = findViewById<CheckBox>(R.id.cbTieneNinos).isChecked
        val experiencia    = findViewById<EditText>(R.id.etExperienciaPrevia).text.toString().trim()
        val motivacion     = findViewById<EditText>(R.id.etMotivacion).text.toString().trim()
        val mensaje        = findViewById<EditText>(R.id.etMensaje).text.toString().trim()

        // Edge cases de validación
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono de contacto es obligatorio",
                Toast.LENGTH_SHORT).show()
            return
        }
        if (motivacion.isEmpty()) {
            Toast.makeText(this, "Cuéntanos por qué quieres adoptar",
                Toast.LENGTH_SHORT).show()
            return
        }

        val idSolicitante = prefs.getInt("idUsuario", -1)
        if (idSolicitante == -1) {
            startActivity(android.content.Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val request = CrearSolicitudRequest(
            mensaje            = mensaje,
            telefonoContacto   = telefono,
            tipoVivienda       = vivienda,
            tieneOtrasMascotas = otrasMascotas,
            tieneNinos         = ninos,
            experienciaPrevia  = experiencia,
            motivacion         = motivacion
        )

        RetrofitClient.instance.crearSolicitud(idMascota, idSolicitante, request)
            .enqueue(object : Callback<SolicitudAdopcion> {
                override fun onResponse(
                    call: Call<SolicitudAdopcion>,
                    response: Response<SolicitudAdopcion>
                ) {
                    when (response.code()) {
                        201 -> {
                            Toast.makeText(this@SolicitudAdopcionActivity,
                                "¡Solicitud enviada! El dueño te contactará pronto",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        409 -> Toast.makeText(this@SolicitudAdopcionActivity,
                            "Ya tienes una solicitud pendiente para esta mascota",
                            Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@SolicitudAdopcionActivity,
                            "No puedes adoptar tu propia mascota",
                            Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@SolicitudAdopcionActivity,
                            "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<SolicitudAdopcion>, t: Throwable) {
                    Toast.makeText(this@SolicitudAdopcionActivity,
                        "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}