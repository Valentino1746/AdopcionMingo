package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CrearUsuarioRequest
import mx.edu.unpa.miandroid.util.ValidationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearrCuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearr_cuenta)

        findViewById<Button>(R.id.btnCrearCuenta).setOnClickListener { crearCuenta() }
    }


    private fun crearCuenta() {
        val nombre     = findViewById<EditText>(R.id.etNombre).text.toString().trim()
        val apPaterno  = findViewById<EditText>(R.id.etApellidoPaterno).text.toString().trim()
        val apMaterno  = findViewById<EditText>(R.id.etApellidoMaterno).text.toString().trim()
        val email      = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val password   = findViewById<EditText>(R.id.etPassword).text.toString()
        val confirmar  = findViewById<EditText>(R.id.etConfirmarPassword).text.toString()

        // Edge cases del lado Android (validación local antes de llamar la API)
        if (nombre.isEmpty() || apPaterno.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        // Validación Gmail — reemplaza el android.util.Patterns anterior
        if (!ValidationUtils.isGmail(email)) {
            Toast.makeText(this, "Solo se aceptan correos Gmail (@gmail.com)", Toast.LENGTH_SHORT).show()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmar) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CrearUsuarioRequest(
            nombre           = nombre,
            apellidoPaterno  = apPaterno,
            apellidoMaterno  = apMaterno,
            email            = email,
            password         = password,
            confirmarPassword = confirmar
        )

        RetrofitClient.instance.crearUsuario(request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {

                        201 -> {
                            Log.d("conexion", "conexion exitosa")
                            Toast.makeText(this@CrearrCuentaActivity,
                                "Cuenta creada. Inicia sesión", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@CrearrCuentaActivity, LoginActivity::class.java))
                            finish()
                        }
                        409 -> Toast.makeText(this@CrearrCuentaActivity,
                            "El email ya está registrado", Toast.LENGTH_SHORT).show()
                        400 -> Toast.makeText(this@CrearrCuentaActivity,
                            "Datos inválidos", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@CrearrCuentaActivity,
                            "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("Sin conexion", "conexion exitosa")
                    Toast.makeText(this@CrearrCuentaActivity,
                        "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}