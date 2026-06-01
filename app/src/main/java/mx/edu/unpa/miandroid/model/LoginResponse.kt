package mx.edu.unpa.miandroid.model

data class LoginResponse(
    val idUsuario: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val email: String
)