package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class CategoriaDisponibilidad(
    val idTipoMascota: Int,
    val descripcion: String,
    val tieneDisponibles: Boolean
) : Serializable