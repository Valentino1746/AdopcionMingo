package mx.edu.unpa.miandroid

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import mx.edu.unpa.miandroid.client.RetrofitClient
import mx.edu.unpa.miandroid.model.UploadFile
import mx.edu.unpa.miandroid.model.Usuario
import mx.edu.unpa.miapp.permissions.PermissionManager
import mx.edu.unpa.miapp.permissions.PermissionState
import mx.edu.unpa.miapp.permissions.PermissionType
import mx.edu.unpa.miapp.permissions.PermissionUtils
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody

class DetalleUsuario : AppCompatActivity() {

    private lateinit var usuario: Usuario
    private var bitmap: Bitmap? = null

    private lateinit var permissionManager: PermissionManager

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        imageBitmap ->
        if(imageBitmap != null){
            bitmap = imageBitmap

            val imageView = findViewById<ImageView>(R.id.imageViewFoto)
            //imageView.setImageBitmap(bitmap)
            // Asignar Foto
            Glide.with(this)
                .load(intent.getStringExtra("foto").toString())
                .into(imageView)

            // Subir foto
            subirImagen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuario = intent.getSerializableExtra("usuario", Usuario::class.java) ?: return

        permissionManager = PermissionManager(this)

        val imageView = findViewById<ImageView>(R.id.imageViewFoto)
        // Asignar Foto
        Glide.with(this)
            //.load(intent.getStringExtra("foto").toString())
            .load(usuario.foto)
            .into(imageView)

        subirImagen()

        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        txtNombre.text = usuario.nombre

        val txtEmail = findViewById<TextView>(R.id.txtEmail)
        txtEmail.text = usuario.email

        val txtTelefono = findViewById<TextView>(R.id.txtTelefono)
        txtTelefono.text = usuario.telefono

    }

    fun tomarFoto(view : View){
        solicitarPermisoCamara()
    }

    private fun solicitarPermisoCamara() {

        permissionManager.requestPermission(PermissionType.CAMERA) {
                state ->
            when(state) {

                PermissionState.GRANTED -> {

                    cameraLauncher.launch(null)
                }

                PermissionState.DENIED -> {

                    Toast.makeText(this, "Permiso de cámara denegado",Toast.LENGTH_SHORT).show()
                }

                PermissionState.PERMANENTLY_DENIED -> {

                    Toast.makeText(this,"Habilita el permiso en configuración",Toast.LENGTH_LONG).show()

                    PermissionUtils.openAppSettings(this)
                }
            }
        }
    }

    private fun subirImagen() {

        val file = File(cacheDir, "foto.jpg")

        val fos = FileOutputStream(file)

        bitmap?.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            fos
        )

        fos.flush()
        fos.close()

        val requestFile = file.asRequestBody("image/*".toMediaType())

        val body =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

        val api = RetrofitClient.instance

        api.uploadImage(body)
            .enqueue(object : retrofit2.Callback<UploadFile> {

                override fun onResponse(call: Call<UploadFile>, response: Response<UploadFile>) {

                    if (response.isSuccessful) {
                        val archivo = response.body()
                        val rutaImagen = archivo?.ruta

                        Toast.makeText(this@DetalleUsuario, "Ruta: ${rutaImagen}", Toast.LENGTH_SHORT).show()
                        actualizarFoto(rutaImagen!!)

                    }
                }

                override fun onFailure(call: Call<UploadFile>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun actualizarFoto(rutaImagen: String){
        usuario.foto = rutaImagen
        RetrofitClient.instance.actualizarUsuario(usuario.id!!, usuario).enqueue(object : retrofit2.Callback<Usuario> {

            override fun onResponse(call: Call<Usuario>,response: Response<Usuario>) {
                if (response.isSuccessful) {
                    Toast.makeText( this@DetalleUsuario,"Foto actualizada",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@DetalleUsuario,t.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

}