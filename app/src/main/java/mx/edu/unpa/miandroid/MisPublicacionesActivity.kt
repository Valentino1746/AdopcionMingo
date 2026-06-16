package mx.edu.unpa.miandroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.adapter.MisMascotasAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Mascota
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisPublicacionesActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: MisMascotasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_publicaciones)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        val recycler = findViewById<RecyclerView>(R.id.recyclerMisPublicaciones)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = MisMascotasAdapter(emptyList(),
            onVerSolicitudes = { mascota ->
                startActivity(Intent(this, SolicitudesRecibidasActivity::class.java).apply {
                    // Filtramos por mascota pasando su id
                    putExtra("idMascota", mascota.idMascota)
                    putExtra("nombreMascota", mascota.nombre)
                })
            }
        )
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnRegresar)
            .setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        cargarMisPublicaciones()
    }

    private fun cargarMisPublicaciones() {
        val idUsuario = prefs.getInt("idUsuario", -1)
        RetrofitClient.instance.misPublicaciones(idUsuario)
            .enqueue(object : Callback<List<Mascota>> {
                override fun onResponse(
                    call: Call<List<Mascota>>,
                    response: Response<List<Mascota>>
                ) {
                    if (response.isSuccessful)
                        adapter.actualizar(response.body() ?: emptyList())
                }
                override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                    Toast.makeText(this@MisPublicacionesActivity,
                        "Error al cargar publicaciones", Toast.LENGTH_SHORT).show()
                }
            })
    }
}