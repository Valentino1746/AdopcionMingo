package mx.edu.unpa.miandroid.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.miandroid.R
import mx.edu.unpa.miandroid.model.CategoriaDisponibilidad

class CategoriaAdapter(
    private var lista: List<CategoriaDisponibilidad>,
    private val onClick: (CategoriaDisponibilidad) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    // Mapa fijo: descripción de BD → drawable local
    // Si el nombre en BD no coincide, muestra imagen genérica
    private val imageMap = mapOf(
        "Perro"   to R.drawable.ic_imagen_perrito_foreground,
        "Gato"    to R.drawable.ic_imagen_gatito_foreground,
        "Hamster" to R.drawable.ic_imagen_hamster_foreground,
        "Loro"    to R.drawable.ic_imagen_loro_foreground
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAnimal:       ImageView = view.findViewById(R.id.imgAnimal)
        val txtNombreTipo:   TextView  = view.findViewById(R.id.txtNombreTipo)
        val txtDisponible:   TextView  = view.findViewById(R.id.txtDisponibilidad)
        val txtChipTipo:     TextView  = view.findViewById(R.id.txtChipTipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = lista[position]

        // Imagen local por nombre
        imageMap[cat.descripcion]?.let { holder.imgAnimal.setImageResource(it) }

        holder.txtNombreTipo.text = cat.descripcion
        holder.txtChipTipo.text   = cat.descripcion

        if (cat.tieneDisponibles) {
            holder.txtDisponible.text      = "Disponibles"
            holder.txtDisponible.setTextColor(
                holder.itemView.context.getColor(R.color.color_disponible)
            )
        } else {
            holder.txtDisponible.text      = "No Disponible"
            holder.txtDisponible.setTextColor(
                holder.itemView.context.getColor(R.color.color_no_disponible)
            )
        }

        holder.itemView.setOnClickListener { onClick(cat) }
    }

    fun actualizar(nuevaLista: List<CategoriaDisponibilidad>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}