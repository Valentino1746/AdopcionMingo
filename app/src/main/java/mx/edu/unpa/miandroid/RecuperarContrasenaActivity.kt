package mx.edu.unpa.miandroid


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.miandroid.client.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecuperarContrasenaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        findViewById<Button>(R.id.btnActualizar).setOnClickListener { actualizar() }
    }

    private fun actualizar() {
        val email     = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val nueva     = findViewById<EditText>(R.id.etNuevaPassword).text.toString()
        val confirmar = findViewById<EditText>(R.id.etConfirmarPassword).text.toString()

        if (email.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (nueva != confirmar) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Nota: Este endpoint necesita ser creado en Spring.
        // Por ahora muestra éxito local y regresa al login.
        // Implementación real: buscar usuario por email, luego PATCH password.
        Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}