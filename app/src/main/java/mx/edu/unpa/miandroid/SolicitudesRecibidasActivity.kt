package mx.edu.unpa.miandroid

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.adapter.SolicitudesRecibidasAdapter
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.SolicitudAdopcion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitudesRecibidasActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: SolicitudesRecibidasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitudes_recibidas)

        prefs = getSharedPreferences("sesion_adoptame", MODE_PRIVATE)

        val nombreMascota = intent.getStringExtra("nombreMascota") ?: "Mascota"
        findViewById<TextView>(R.id.txtTituloSolicitudes).text =
            "Solicitudes para $nombreMascota"

        val recycler = findViewById<RecyclerView>(R.id.recyclerSolicitudesRecibidas)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = SolicitudesRecibidasAdapter(emptyList(),
            onAceptar = { solicitud -> resolverSolicitud(solicitud.idSolicitud, true) },
            onRechazar = { solicitud -> resolverSolicitud(solicitud.idSolicitud, false) }
        )
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnRegresar)
            .setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        cargar()
    }

    private fun cargar() {
        val idUsuario = prefs.getInt("idUsuario", -1)
        RetrofitClient.instance.solicitudesRecibidas(idUsuario)
            .enqueue(object : Callback<List<SolicitudAdopcion>> {
                override fun onResponse(
                    call: Call<List<SolicitudAdopcion>>,
                    response: Response<List<SolicitudAdopcion>>
                ) {
                    if (response.isSuccessful) {
                        // Filtrar por mascota si se pasó idMascota
                        val idMascota = intent.getIntExtra("idMascota", -1)
                        val lista = response.body() ?: emptyList()
                        adapter.actualizar(
                            if (idMascota != -1) lista.filter { it.idMascota == idMascota }
                            else lista
                        )
                    }
                }
                override fun onFailure(call: Call<List<SolicitudAdopcion>>, t: Throwable) {
                    Toast.makeText(this@SolicitudesRecibidasActivity,
                        "Error al cargar solicitudes", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun resolverSolicitud(idSolicitud: Int, aceptar: Boolean) {
        val call = if (aceptar)
            RetrofitClient.instance.aceptarSolicitud(idSolicitud)
        else
            RetrofitClient.instance.rechazarSolicitud(idSolicitud)

        call.enqueue(object : Callback<SolicitudAdopcion> {
            override fun onResponse(
                call: Call<SolicitudAdopcion>,
                response: Response<SolicitudAdopcion>
            ) {
                if (response.isSuccessful) {
                    val msg = if (aceptar) "¡Solicitud aceptada!" else "Solicitud rechazada"
                    Toast.makeText(this@SolicitudesRecibidasActivity,
                        msg, Toast.LENGTH_SHORT).show()
                    cargar() // Refrescar lista
                } else {
                    Toast.makeText(this@SolicitudesRecibidasActivity,
                        "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SolicitudAdopcion>, t: Throwable) {
                Toast.makeText(this@SolicitudesRecibidasActivity,
                    "Sin conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}