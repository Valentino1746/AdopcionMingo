package mx.edu.unpa.miandroid

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.adapter.MisSolicitudesAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.SolicitudAdopcion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisSolicitudesActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: MisSolicitudesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_solicitudes)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        val recycler = findViewById<RecyclerView>(R.id.recyclerMisSolicitudes)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MisSolicitudesAdapter(emptyList())
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnRegresar)
            .setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        cargar()
    }

    private fun cargar() {
        val idUsuario = prefs.getInt("idUsuario", -1)
        RetrofitClient.instance.misSolicitudes(idUsuario)
            .enqueue(object : Callback<List<SolicitudAdopcion>> {
                override fun onResponse(
                    call: Call<List<SolicitudAdopcion>>,
                    response: Response<List<SolicitudAdopcion>>
                ) {
                    if (response.isSuccessful)
                        adapter.actualizar(response.body() ?: emptyList())
                }
                override fun onFailure(call: Call<List<SolicitudAdopcion>>, t: Throwable) {
                    Toast.makeText(this@MisSolicitudesActivity,
                        "Error al cargar solicitudes", Toast.LENGTH_SHORT).show()
                }
            })
    }
}