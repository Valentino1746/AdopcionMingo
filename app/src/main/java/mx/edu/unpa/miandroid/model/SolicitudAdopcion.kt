package mx.edu.unpa.miandroid.model

import java.io.Serializable

data class SolicitudAdopcion(
    val idSolicitud: Int,
    val idMascota: Int,
    val nombreMascota: String,
    val urlFotoMascota: String?,
    val tipoMascota: String,
    val idSolicitante: Int,
    val nombreSolicitante: String,
    val telefonoSolicitante: String?,
    val mensaje: String?,
    val telefonoContacto: String?,
    val tipoVivienda: String?,
    val tieneOtrasMascotas: Boolean,
    val tieneNinos: Boolean,
    val experienciaPrevia: String?,
    val motivacion: String?,
    val estadoSolicitud: String,
    val fechaSolicitud: String,
    val fechaResolucion: String?
) : Serializable