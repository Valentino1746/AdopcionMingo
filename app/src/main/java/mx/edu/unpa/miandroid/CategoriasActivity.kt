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
        supportActionBar?.title = "AdoptaMe"

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
        cargarCategorias() // Refresca disponibilidad al volver
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_categorias, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_agregar -> {
                startActivity(Intent(this, RegistrarMascotaActivity::class.java))
                true
            }
            R.id.action_logout -> {
                prefs.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}