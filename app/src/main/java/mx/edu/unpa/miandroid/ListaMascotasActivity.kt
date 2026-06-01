package mx.edu.unpa.miandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.adapter.MascotaAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Mascota
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaMascotasActivity : AppCompatActivity() {

    private lateinit var adapter: MascotaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_mascotas)

        val idTipo     = intent.getIntExtra("idTipoMascota", -1)
        val nombreTipo = intent.getStringExtra("nombreTipo") ?: "Mascotas"

        // Título en el header
        findViewById<android.widget.TextView>(R.id.txtTituloLista).text = nombreTipo

        // Botón regresar del header inferior — fácil de tocar
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegresar)
            .setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        val recycler = findViewById<RecyclerView>(R.id.recyclerMascotas)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MascotaAdapter(emptyList()) { mascota ->
            val intent = Intent(this, DetalleMascotaActivity::class.java).apply {
                putExtra(DetalleMascotaActivity.EXTRA_MASCOTA, mascota)
            }
            startActivity(intent)
        }
        recycler.adapter = adapter

        cargarMascotas(idTipo)
    }

    private fun cargarMascotas(idTipo: Int) {
        val call = if (idTipo != -1)
            RetrofitClient.instance.getMascotasPorTipo(idTipo)
        else
            RetrofitClient.instance.getMascotas()

        call.enqueue(object : Callback<List<Mascota>> {
            override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                if (response.isSuccessful) {
                    adapter.actualizar(response.body() ?: emptyList())
                }
            }
            override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                Toast.makeText(this@ListaMascotasActivity,
                    "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}