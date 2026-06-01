package mx.edu.unpa.miandroid.util


object ValidationUtils {

    // Solo acepta @gmail.com — edge cases cubiertos:
    // - espacios al inicio/fin (trim en el caller)
    // - mayúsculas: GMAIL.COM, Gmail.Com → se normaliza con lowercase
    // - dominio incorrecto: @hotmail, @yahoo → rechazado
    fun isGmail(email: String): Boolean {
        return email.trim().lowercase().endsWith("@gmail.com")
    }
}