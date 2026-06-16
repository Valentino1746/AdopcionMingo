package mx.edu.unpa.miandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Mascota

class MisMascotasAdapter(
    private var lista: List<Mascota>,
    private val onVerSolicitudes: (Mascota) -> Unit
) : RecyclerView.Adapter<MisMascotasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView   = view.findViewById(R.id.imgMascota)
        val nombre: TextView = view.findViewById(R.id.txtNombreMascota)
        val estado: TextView = view.findViewById(R.id.txtEstadoMascota)
        val tipo: TextView   = view.findViewById(R.id.txtTipoMascota)
        val btnSolicitudes: Button = view.findViewById(R.id.btnVerSolicitudes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mi_mascota, parent, false)
    )

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = lista[position]
        holder.nombre.text = m.nombre
        holder.tipo.text   = m.tipoMascotaDescripcion
        holder.estado.text = m.estadoAdopcion.replace("_", " ")

        val fallback = R.drawable.ic_launcher_foreground
        if (!m.urlFoto.isNullOrBlank()) {
            Glide.with(holder.itemView.context)
                .load(RetrofitClient.BASE_URL.trimEnd('/') + m.urlFoto)
                .placeholder(fallback).error(fallback).centerCrop()
                .into(holder.img)
        } else {
            holder.img.setImageResource(fallback)
        }

        // Solo mostramos botón de solicitudes si la mascota tiene actividad relevante
        holder.btnSolicitudes.setOnClickListener { onVerSolicitudes(m) }
    }

    fun actualizar(nuevaLista: List<Mascota>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}