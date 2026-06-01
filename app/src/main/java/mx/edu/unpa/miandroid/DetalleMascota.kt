package mx.edu.unpa.miandroid

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.Mascota

class DetalleMascotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_mascota)

        // Edge case: si el intent no trae la mascota, cerramos inmediatamente
        val mascota = intent.getSerializableExtra(EXTRA_MASCOTA) as? Mascota
            ?: run { finish(); return }

        bindViews(mascota)

        findViewById<MaterialButton>(R.id.btnRegresarDetalle)
            .setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun bindViews(mascota: Mascota) {
        val imgFoto        = findViewById<ImageView>(R.id.imgDetalleMascota)
        val txtNombre      = findViewById<TextView>(R.id.txtDetalleNombre)
        val txtEstado      = findViewById<TextView>(R.id.txtDetalleEstado)
        val txtTipo        = findViewById<TextView>(R.id.txtDetalleTipo)
        val txtRaza        = findViewById<TextView>(R.id.txtDetalleRaza)
        val txtSexo        = findViewById<TextView>(R.id.txtDetalleSexo)
        val txtEdad        = findViewById<TextView>(R.id.txtDetalleEdad)
        val txtDescripcion = findViewById<TextView>(R.id.txtDetalleDescripcion)
        val txtFecha       = findViewById<TextView>(R.id.txtDetalleFecha)

        txtNombre.text      = mascota.nombre
        txtEstado.text      = mascota.estadoAdopcion.replace("_", " ")
        txtTipo.text        = mascota.tipoMascotaDescripcion
        txtRaza.text        = mascota.raza?.ifBlank { "Mestizo" } ?: "Mestizo"
        txtSexo.text        = mascota.sexo
        txtEdad.text        = mascota.edadAproximada?.ifBlank { "No especificada" } ?: "No especificada"
        txtDescripcion.text = mascota.descripcion?.ifBlank { "Sin descripción" } ?: "Sin descripción"
        txtFecha.text       = mascota.fechaPublicacion

        // Color del badge de estado
        val (bgColor, textColor) = when (mascota.estadoAdopcion) {
            "Disponible" -> Pair(R.color.badge_disponible_bg, R.color.badge_disponible_text)
            "En_proceso" -> Pair(R.color.badge_proceso_bg,    R.color.badge_proceso_text)
            "Adoptado"   -> Pair(R.color.badge_adoptado_bg,   R.color.badge_adoptado_text)
            else         -> Pair(R.color.badge_disponible_bg, R.color.badge_disponible_text)
        }
        txtEstado.setBackgroundColor(getColor(bgColor))
        txtEstado.setTextColor(getColor(textColor))

        // Imagen: remota con Glide o fallback local
        val fallbackRes = ANIMAL_IMAGE_MAP[mascota.tipoMascotaDescripcion]
            ?: R.drawable.ic_launcher_foreground

        if (!mascota.urlFoto.isNullOrBlank()) {
            val urlCompleta = RetrofitClient.BASE_URL.trimEnd('/') + mascota.urlFoto
            Glide.with(this)
                .load(urlCompleta)
                .placeholder(fallbackRes)
                .error(fallbackRes)
                .centerCrop()
                .into(imgFoto)
        } else {
            imgFoto.setImageResource(fallbackRes)
        }
    }

    companion object {
        const val EXTRA_MASCOTA = "mascota"

        // Centralizado aquí y en MascotaAdapter — candidato a un AnimalImageProvider
        // si el proyecto escala; por ahora evita duplicar el Map en cada bind
        private val ANIMAL_IMAGE_MAP = mapOf(
            "Perro"   to R.drawable.ic_imagen_perrito_foreground,
            "Gato"    to R.drawable.ic_imagen_gatito_foreground,
            "Hamster" to R.drawable.ic_imagen_hamster_foreground,
            "Loro"    to R.drawable.ic_imagen_loro_foreground
        )
    }
}