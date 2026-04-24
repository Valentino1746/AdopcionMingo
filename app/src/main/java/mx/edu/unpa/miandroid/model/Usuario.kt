package mx.edu.unpa.miandroid.model

import android.text.Editable

data class Usuario(
    var id: Int? = null,
    var nombre: String,
    var apellidoPaterno: String,
    var apellidoMaterno: String? = null,
    var email: String,
    var telefono: String? = null,
    var contrasena: String,
    var activo: Boolean = false,
    var fechaRegistro: String? = null

)
