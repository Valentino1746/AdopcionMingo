package mx.edu.unpa.miandroid.model

data class PerfilResponse(
    val idUsuario: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val email: String,
    val telefono: String?,
    val urlFotoPerfil: String?,
    val fechaRegistro: String
)