package mx.edu.unpa.miandroid


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.CategoriaAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CategoriaDisponibilidad
import androidx.activity.OnBackPressedCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriasActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        // Botón físico de regresar → cierra sesión y va al login
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cerrarSesionYVolverAlLogin()
            }
        })

        // FAB para agregar mascota — siempre visible y accesible
        findViewById<FloatingActionButton>(R.id.fabAgregar).setOnClickListener {
            startActivity(Intent(this, RegistrarMascotaActivity::class.java))
        }

        // Botón logout en la parte inferior
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout)
            .setOnClickListener {
                cerrarSesionYVolverAlLogin()
            }

        val recycler = findViewById<RecyclerView>(R.id.recyclerCategorias)
        recycler.layoutManager = GridLayoutManager(this, 2)

        adapter = CategoriaAdapter(emptyList()) { categoria ->
            val intent = Intent(this, ListaMascotasActivity::class.java).apply {
                putExtra("idTipoMascota", categoria.idTipoMascota)
                putExtra("nombreTipo", categoria.descripcion)
            }
            startActivity(intent)
        }
        recycler.adapter = adapter

        cargarCategorias()
    }

    override fun onResume() {
        super.onResume()
        cargarCategorias()
    }

    private fun cargarCategorias() {
        RetrofitClient.instance.getCategoriasConDisponibilidad()
            .enqueue(object : Callback<List<CategoriaDisponibilidad>> {
                override fun onResponse(
                    call: Call<List<CategoriaDisponibilidad>>,
                    response: Response<List<CategoriaDisponibilidad>>
                ) {
                    if (response.isSuccessful) {
                        adapter.actualizar(response.body() ?: emptyList())
                    }
                }
                override fun onFailure(call: Call<List<CategoriaDisponibilidad>>, t: Throwable) {
                    Toast.makeText(this@CategoriasActivity,
                        "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cerrarSesionYVolverAlLogin() {
        prefs.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java).apply {
            // Limpia el back stack completo para que no pueda volver con el back
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}