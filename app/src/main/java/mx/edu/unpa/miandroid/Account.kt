/*package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Usuario
import retrofit2.Call
import retrofit2.Response

class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun create(view: View){
        val nombre = findViewById<EditText>(R.id.txtNombre).text.toString()
        val apellidoPaterno = findViewById<EditText>(R.id.txtApellidoPaterno).text.toString()
        val apellidoMaterno = findViewById<EditText>(R.id.txtApellidoMaterno).text.toString()
        val email = findViewById<EditText>(R.id.txtEmail).text.toString()
        val contrasena = findViewById<EditText>(R.id.txtContrasena).text.toString()




        val nuevo = Usuario(
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            email = email,
            contrasena = contrasena,
        )

        RetrofitClient.instance.crearUsuario(nuevo).enqueue(object : retrofit2.Callback<Usuario>{
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                Log.d("RETROFIT_CODE", "Código: ${response.code()}")
                Log.d("RETROFIT_BODY", "Body: ${response.body()}")
                Log.d("RETROFIT_ERROR", "Error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    val nuevoUsuario = response.body()
                    if (nuevoUsuario != null) {
                        Log.d("RETROFIT_CREADO", "Usuario creado: ${nuevoUsuario.nombre}")
                    } else {
                        Log.d("RETROFIT_CREADO", "Response exitoso pero body es null")
                    }
                } else {
                    Log.d("RETROFIT_FAIL", "No exitoso: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.d("RETROFIT_CUENTA", "Fallo: ${t.message}" )
                t.printStackTrace()
            }

        })

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }


}
*/
