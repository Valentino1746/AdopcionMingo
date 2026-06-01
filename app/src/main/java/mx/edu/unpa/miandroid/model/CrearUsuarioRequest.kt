package mx.edu.unpa.miandroid.model


data class CrearUsuarioRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val email: String,
    val password: String,
    val confirmarPassword: String,
    val telefono: String? = null
)