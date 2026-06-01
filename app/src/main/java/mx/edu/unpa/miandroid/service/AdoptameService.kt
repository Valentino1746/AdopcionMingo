package mx.edu.unpa.miandroid.service


import mx.edu.unpa.*
import mx.edu.unpa.miandroid.model.CategoriaDisponibilidad
import mx.edu.unpa.miandroid.model.CrearMascotaRequest
import mx.edu.unpa.miandroid.model.CrearUsuarioRequest
import mx.edu.unpa.miandroid.model.LoginRequest
import mx.edu.unpa.miandroid.model.LoginResponse
import mx.edu.unpa.miandroid.model.Mascota
import mx.edu.unpa.miandroid.model.TipoMascota
import mx.edu.unpa.miandroid.model.UploadFile
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface AdoptameService {

    // ── Usuarios ─────────────────────────────────────────────
    @POST("api/usuarios/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/usuarios")
    fun crearUsuario(@Body request: CrearUsuarioRequest): Call<Void>

    @PUT("api/usuarios/{id}/password")
    fun recuperarPassword(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): Call<Void>

    // ── Tipos / Categorías ────────────────────────────────────
    @GET("api/tipos-mascota")
    fun getTiposMascota(): Call<List<TipoMascota>>

    @GET("api/tipos-mascota/disponibilidad")
    fun getCategoriasConDisponibilidad(): Call<List<CategoriaDisponibilidad>>

    // ── Mascotas ──────────────────────────────────────────────
    @GET("api/mascotas")
    fun getMascotas(): Call<List<Mascota>>

    @GET("api/mascotas")
    fun getMascotasPorTipo(@Query("idTipo") idTipo: Int): Call<List<Mascota>>

    @POST("api/mascotas")
    fun crearMascota(
        @Query("idDonador") idDonador: Int,
        @Query("idTipo") idTipo: Int,
        @Body request: CrearMascotaRequest
    ): Call<Mascota>

    // Upload de imagen — mismo endpoint que el proyecto viejo
    @Multipart
    @POST("api/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<UploadFile>
}