package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.SolicitudAdopcion

class MisSolicitudesAdapter(
    private var lista: List<SolicitudAdopcion>
) : RecyclerView.Adapter<MisSolicitudesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView        = view.findViewById(R.id.imgMascotaSolicitud)
        val nombreMascota: TextView = view.findViewById(R.id.txtNombreMascotaSolicitud)
        val tipo: TextView        = view.findViewById(R.id.txtTipoMascotaSolicitud)
        val estado: TextView      = view.findViewById(R.id.txtEstadoSolicitud)
        val fecha: TextView       = view.findViewById(R.id.txtFechaSolicitud)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mi_solicitud, parent, false)
    )

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = lista[position]
        holder.nombreMascota.text = s.nombreMascota
        holder.tipo.text          = s.tipoMascota
        holder.estado.text        = s.estadoSolicitud
        holder.fecha.text         = s.fechaSolicitud.take(10)

        val ctx = holder.itemView.context
        val (bgColor, textColor) = when (s.estadoSolicitud) {
            "Aprobada"  -> Pair(R.color.badge_disponible_bg, R.color.badge_disponible_text)
            "Rechazada" -> Pair(R.color.badge_adoptado_bg,   R.color.badge_adoptado_text)
            else        -> Pair(R.color.badge_proceso_bg,    R.color.badge_proceso_text)
        }
        holder.estado.setBackgroundColor(ctx.getColor(bgColor))
        holder.estado.setTextColor(ctx.getColor(textColor))

        val fallback = R.drawable.ic_launcher_foreground
        if (!s.urlFotoMascota.isNullOrBlank()) {
            Glide.with(ctx)
                .load(RetrofitClient.BASE_URL.trimEnd('/') + s.urlFotoMascota)
                .placeholder(fallback).error(fallback).centerCrop()
                .into(holder.img)
        } else {
            holder.img.setImageResource(fallback)
        }
    }

    fun actualizar(nuevaLista: List<SolicitudAdopcion>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}