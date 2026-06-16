package mx.edu.unpa.miandroid.model

data class CrearSolicitudRequest(
    val mensaje: String,
    val telefonoContacto: String,
    val tipoVivienda: String,
    val tieneOtrasMascotas: Boolean,
    val tieneNinos: Boolean,
    val experienciaPrevia: String,
    val motivacion: String
)