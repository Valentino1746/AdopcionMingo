package mx.edu.unpa.miandroid

import kotlin.jvm.java

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.LoginRequest
import mx.edu.unpa.miandroid.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        // Si ya hay sesión guardada, saltar directo a Categorías
        if (prefs.getInt("idUsuario", -1) != -1) {
            goToCategorias()
            return
        }

        setContentView(R.layout.activity_login)

        val btnLogin       = findViewById<Button>(R.id.btnLogin)
        val txtOlvide      = findViewById<TextView>(R.id.txtOlvidePassword)
        val txtRegistrarse = findViewById<TextView>(R.id.txtRegistrarse)

        btnLogin.setOnClickListener { doLogin() }

        txtOlvide.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenaActivity::class.java))
        }

        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, CrearrCuentaActivity::class.java))
        }
    }

    private fun doLogin() {
        val email    = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()

        // Edge case: campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.login(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    when (response.code()) {
                        200 -> {
                            val user = response.body()!!
                            prefs.edit()
                                .putInt("idUsuario", user.idUsuario)
                                .putString("nombre", user.nombre)
                                .putString("email", user.email)
                                .apply()
                            goToCategorias()
                            finish()
                        }
                        401 -> Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        403 -> Toast.makeText(this@LoginActivity, "Cuenta desactivada", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@LoginActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun goToCategorias() {
        startActivity(Intent(this, CategoriasActivity::class.java))
    }
}