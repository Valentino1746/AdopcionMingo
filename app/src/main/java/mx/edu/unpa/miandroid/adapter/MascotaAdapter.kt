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

class MascotaAdapter(
    private var lista: List<Mascota>,
    private val onClick: (Mascota) -> Unit   // ← SRP: el adapter notifica, el caller decide
) : RecyclerView.Adapter<MascotaAdapter.ViewHolder>() {

    companion object {
        // Fix del code smell: el Map se crea UNA sola vez, no en cada bind
        private val ANIMAL_IMAGE_MAP = mapOf(
            "Perro"   to R.drawable.ic_imagen_perrito_foreground,
            "Gato"    to R.drawable.ic_imagen_gatito_foreground,
            "Hamster" to R.drawable.ic_imagen_hamster_foreground,
            "Loro"    to R.drawable.ic_imagen_loro_foreground
        )
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMascota:  ImageView = view.findViewById(R.id.imgMascota)
        val txtNombre:   TextView  = view.findViewById(R.id.txtNombreMascota)
        val txtTipo:     TextView  = view.findViewById(R.id.txtTipoMascota)
        val txtRaza:     TextView  = view.findViewById(R.id.txtRaza)
        val txtSexo:     TextView  = view.findViewById(R.id.txtSexo)
        val txtEstado:   TextView  = view.findViewById(R.id.txtEstadoDescripcion)
        val btnAdopcion: Button    = view.findViewById(R.id.btnEnAdopcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mascota = lista[position]
        val ctx = holder.itemView.context

        val fallbackRes = ANIMAL_IMAGE_MAP[mascota.tipoMascotaDescripcion]
            ?: R.drawable.ic_launcher_foreground

        holder.txtNombre.text = mascota.nombre
        holder.txtTipo.text   = mascota.tipoMascotaDescripcion
        holder.txtRaza.text   = mascota.raza ?: "Mestizo"
        holder.txtSexo.text   = mascota.sexo
        holder.txtEstado.text = mascota.estadoAdopcion.replace("_", " ")

        if (!mascota.urlFoto.isNullOrBlank()) {
            val urlCompleta = RetrofitClient.BASE_URL.trimEnd('/') + mascota.urlFoto
            Glide.with(ctx)
                .load(urlCompleta)
                .placeholder(fallbackRes)
                .error(fallbackRes)
                .centerCrop()
                .into(holder.imgMascota)
        } else {
            holder.imgMascota.setImageResource(fallbackRes)
        }

        val (bgColor, textColor) = when (mascota.estadoAdopcion) {
            "Disponible" -> Pair(R.color.badge_disponible_bg, R.color.badge_disponible_text)
            "En_proceso" -> Pair(R.color.badge_proceso_bg,    R.color.badge_proceso_text)
            "Adoptado"   -> Pair(R.color.badge_adoptado_bg,   R.color.badge_adoptado_text)
            else         -> Pair(R.color.badge_disponible_bg, R.color.badge_disponible_text)
        }
        holder.btnAdopcion.setBackgroundColor(ctx.getColor(bgColor))
        holder.btnAdopcion.setTextColor(ctx.getColor(textColor))

        // Click en la tarjeta completa
        holder.itemView.setOnClickListener { onClick(mascota) }
    }

    fun actualizar(nuevaLista: List<Mascota>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}