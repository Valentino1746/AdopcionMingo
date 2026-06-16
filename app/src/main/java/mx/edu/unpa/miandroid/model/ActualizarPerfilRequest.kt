package mx.edu.unpa.miandroid.model

data class ActualizarPerfilRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val telefono: String?,
    val urlFotoPerfil: String?
)