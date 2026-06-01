package mx.edu.unpa.miandroid.model

data class CrearMascotaRequest(
    val nombre: String,
    val raza: String,
    val sexo: String,
    val edadAproximada: String,
    val descripcion: String
)