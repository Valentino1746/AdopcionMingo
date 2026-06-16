package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.SolicitudAdopcion

class SolicitudesRecibidasAdapter(
    private var lista: List<SolicitudAdopcion>,
    private val onAceptar: (SolicitudAdopcion) -> Unit,
    private val onRechazar: (SolicitudAdopcion) -> Unit
) : RecyclerView.Adapter<SolicitudesRecibidasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombreSolicitante: TextView = view.findViewById(R.id.txtNombreSolicitante)
        val txtTelefono: TextView          = view.findViewById(R.id.txtTelefonoSolicitante)
        val txtVivienda: TextView          = view.findViewById(R.id.txtVivienda)
        val txtOtrasMascotas: TextView     = view.findViewById(R.id.txtOtrasMascotas)
        val txtNinos: TextView             = view.findViewById(R.id.txtNinos)
        val txtExperiencia: TextView       = view.findViewById(R.id.txtExperiencia)
        val txtMotivacion: TextView        = view.findViewById(R.id.txtMotivacion)
        val txtEstado: TextView            = view.findViewById(R.id.txtEstadoSolicitud)
        val btnAceptar: Button             = view.findViewById(R.id.btnAceptarSolicitud)
        val btnRechazar: Button            = view.findViewById(R.id.btnRechazarSolicitud)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_solicitud_recibida, parent, false)
    )

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = lista[position]

        holder.txtNombreSolicitante.text = s.nombreSolicitante
        holder.txtTelefono.text  = s.telefonoSolicitante ?: "No especificado"
        holder.txtVivienda.text  = s.tipoVivienda ?: "No especificada"
        holder.txtOtrasMascotas.text = if (s.tieneOtrasMascotas) "Sí" else "No"
        holder.txtNinos.text     = if (s.tieneNinos) "Sí" else "No"
        holder.txtExperiencia.text = s.experienciaPrevia?.ifBlank { "Sin experiencia previa" }
            ?: "Sin experiencia previa"
        holder.txtMotivacion.text = s.motivacion ?: "Sin descripción"
        holder.txtEstado.text    = s.estadoSolicitud

        // Solo mostrar botones si está pendiente
        val isPendiente = s.estadoSolicitud == "Pendiente"
        holder.btnAceptar.visibility  = if (isPendiente) View.VISIBLE else View.GONE
        holder.btnRechazar.visibility = if (isPendiente) View.VISIBLE else View.GONE

        holder.btnAceptar.setOnClickListener  { onAceptar(s) }
        holder.btnRechazar.setOnClickListener { onRechazar(s) }
    }

    fun actualizar(nuevaLista: List<SolicitudAdopcion>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}