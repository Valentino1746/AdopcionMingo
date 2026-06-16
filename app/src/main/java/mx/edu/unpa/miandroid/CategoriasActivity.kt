package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mx.edu.unpa.miandroid.adapter.CategoriaAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.CategoriaDisponibilidad
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = cerrarSesionYVolverAlLogin()
        })

        findViewById<FloatingActionButton>(R.id.fabAgregar).setOnClickListener {
            startActivity(Intent(this, RegistrarMascotaActivity::class.java))
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout)
            .setOnClickListener { cerrarSesionYVolverAlLogin() }

        val recycler = findViewById<RecyclerView>(R.id.recyclerCategorias)
        recycler.layoutManager = GridLayoutManager(this, 2)

        adapter = CategoriaAdapter(emptyList()) { categoria ->
            startActivity(Intent(this, ListaMascotasActivity::class.java).apply {
                putExtra("idTipoMascota", categoria.idTipoMascota)
                putExtra("nombreTipo", categoria.descripcion)
            })
        }
        recycler.adapter = adapter

        cargarCategorias()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_categorias, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val idUsuario = prefs.getInt("idUsuario", -1)
        return when (item.itemId) {
            R.id.action_perfil -> {
                startActivity(Intent(this, PerfilActivity::class.java))
                true
            }
            R.id.action_mis_publicaciones -> {
                startActivity(Intent(this, MisPublicacionesActivity::class.java))
                true
            }
            R.id.action_mis_solicitudes -> {
                startActivity(Intent(this, MisSolicitudesActivity::class.java))
                true
            }
            R.id.action_logout -> {
                cerrarSesionYVolverAlLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                    if (response.isSuccessful)
                        adapter.actualizar(response.body() ?: emptyList())
                }
                override fun onFailure(call: Call<List<CategoriaDisponibilidad>>, t: Throwable) {
                    Toast.makeText(this@CategoriasActivity,
                        "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cerrarSesionYVolverAlLogin() {
        prefs.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}