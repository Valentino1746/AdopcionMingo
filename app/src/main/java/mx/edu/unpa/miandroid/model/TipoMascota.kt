package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class TipoMascota(
    val idTipoMascota: Int,
    val descripcion: String,
    val activo: Boolean
) : Serializable