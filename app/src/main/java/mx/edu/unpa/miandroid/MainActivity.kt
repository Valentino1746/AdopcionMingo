package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
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

class MainActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences

    private var listaUsuarios: List<Usuario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val usuario = prefs.getString("usuario", null)

        if(usuario != null ){
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            
        }



        // Se inicializa variables, se carga los elementos de XML(Botones, caja de texto, etc...)
        Log.d("MAIN_ACTIVITY", "Metodo onCreate")
    }

    override fun onStart(){
        super.onStart()
        // Se cargan los recursos vizuales, el layout ya es visible. Pero el usuario no puede interactuar con la pantalla
        Log.d("MAIN_ACTIVITY", "Metodo onStart")
    }

    override fun onResume(){
        super.onResume()
        // Ya se cargo el layout, el usuario puede interactuar los elemnentos en pantalla
        Log.d("MAIN_ACTIVITY", "Metodo onResume")

        RetrofitClient.instance.getUsuario().enqueue(object : retrofit2.Callback<List<Usuario>>{
            override fun onResponse(
                call : Call<List<Usuario>?>,
                response : Response<List<Usuario>?>) {

                if(response.isSuccessful){
                    Log.d("RETROFIT", "Conexion exitosa!!")

                    //val lista = response.body()
                    listaUsuarios = response.body() ?: emptyList()
                    listaUsuarios?.forEach {
                        Log.d("DB_ANDROID", it.nombre + it.apellidoPaterno)
                    }
                }

            }

            override fun onFailure(call: Call<List<Usuario>?>, t: Throwable) {
                Log.d("RETROFIT", "Fallo: ${t.message}")
                t.printStackTrace()
            }

        })
    }



    override fun onPause(){
        super.onPause()
        // El layout pierde foco, pero se puede ver en pantalla
        // Entra una mensaje externo o notificacion push
        // Se abre otra actividad (Alarma)
        Log.d("MAIN_ACTIVITY", "Metodo onPause")
    }

    override fun onStop(){
        super.onStop()
        // Se cambia a otra layout(pantalla)
        // Se minimiza el layout
        Log.d("MAIN_ACTIVITY", "Metodo onStop")
    }

    override fun onRestart() {
        super.onRestart()
        // Se ejecuta cuando se regresa al layout (Pantalla(Actividad))
        Log.d("MAIN_ACTIVITY", "Metodo onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        // El layout se destruye
        Log.d("MAIN_ACTIVITY", "Metodo onDestroy")
    }

    fun login(view: View){
        val usuario = findViewById<EditText>(R.id.txtUsuario).getText().toString()
        val contrasena = findViewById<EditText>(R.id.txtContrasena).getText().toString()

        if (usuario.isNotEmpty() && contrasena.isNotEmpty()){

            RetrofitClient.instance.getUsuarioByEmail(usuario).enqueue(object : retrofit2.Callback<Usuario>{
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful){
                        Log.d("RETROFIT", "Conexion exitosa!!")
                        val usuarioByEmail = response.body()

                        if(usuarioByEmail?.contrasena == contrasena){
                            Log.d("DB_ANDROID", "Usuario creado " + usuarioByEmail?.nombre.toString())

                            val editar = prefs.edit()

                            editar.putString("usuario", usuario)
                            editar.putString("contrasena", contrasena)
                            editar.apply()

                            val intent = Intent(this@MainActivity, Dashboard::class.java)
                            intent.putExtra("NOMBRE_USUARIO", usuarioByEmail?.nombre)
                            intent.putExtra("EMAIL", usuarioByEmail?.email)
                            startActivity(intent)
                            finish() // Para que no pueda volver al login con el botón atrás

                        }

                    } else if(response.code() == 404){
                        Log.d("RETROFIT", "Usuario no existe")
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Log.d("RETROFIT", "Fallo: ${t.message}" )
                    t.printStackTrace()
                }

            })
        }
    }

    fun create (view: View){
        val intent = Intent(this, Account::class.java)
        startActivity(intent)
    }

}