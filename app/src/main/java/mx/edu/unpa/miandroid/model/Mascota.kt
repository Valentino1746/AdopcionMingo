package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class Mascota(
    val idMascota: Int,
    val nombre: String,
    val raza: String?,
    val sexo: String,
    val edadAproximada: String?,
    val descripcion: String?,
    val estadoAdopcion: String,
    val tipoMascotaDescripcion: String,
    val idTipoMascota: Int,
    val fechaPublicacion: String
) : Serializable