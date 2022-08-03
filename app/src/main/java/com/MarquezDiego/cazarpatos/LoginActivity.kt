package com.MarquezDiego.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.MarquezDiego.cazarpatos.R.layout.activity_login
import com.MarquezDiego.cazarpatos.storage.EncryptedSharedPreferencesManager
import com.MarquezDiego.cazarpatos.storage.FileExternalManager
import com.MarquezDiego.cazarpatos.storage.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    //lateinit var manejadorArchivo: FileHandler
    lateinit var manejadorArchivo :FileHandler
    lateinit var editTextEmail:EditText
    lateinit var editTextPassword:EditText
    lateinit var buttonLogin:Button
    lateinit var buttonNewUser:Button
    lateinit var checkBoxRecordarme: CheckBox
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(activity_login)
        //Inicialización de variables
        //manejadorArchivo = SharedPreferencesManager(this)
        //manejadorArchivo = EncryptedSharedPreferencesManager(this)
        manejadorArchivo = FileExternalManager(this)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)
        // Initialize Firebase Auth
        auth = Firebase.auth
        LeerDatosDePreferencias()
        //LeerDatosDePreferenciasEncrypt()
        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if(!ValidarDatosRequeridos())
                return@setOnClickListener
            //Guardar datos en preferencias.
            GuardarDatosEnPreferencias()
            //Si pasa validación de datos requeridos, ir a pantalla principal
            //val intencion = Intent(this, MainActivity::class.java)
            //intencion.putExtra(EXTRA_LOGIN, email)
            //startActivity(intencion)
            AutenticarUsuario(email, clave)


        }
        buttonNewUser.setOnClickListener{
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if(!ValidarDatosRequeridos())
                return@setOnClickListener
            SignUpNewUser(email,clave)
            AutenticarUsuario(email, clave)
        }
        mediaPlayer=MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }
    fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun SignUpNewUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(EXTRA_LOGIN, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "New user saved.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(EXTRA_LOGIN, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun GuardarDatosEnPreferencias(){
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val listadoAGrabar:Pair<String,String>
        if(checkBoxRecordarme.isChecked){
            listadoAGrabar = email to clave
        }
        else{
            listadoAGrabar ="" to ""
        }
        manejadorArchivo.SaveInformation(listadoAGrabar)
    }
//    private fun GuardarDatosEncriptadosEnPreferencias(){
//        val email = editTextEmail.text.toString()
//        val clave = editTextPassword.text.toString()
//        val listadoAGrabar:Pair<String,String>
//        if(checkBoxRecordarme.isChecked){
//            listadoAGrabar = email to clave
//        }
//        else{
//            listadoAGrabar ="" to ""
//        }
//        manejadorArchivoEncrypt.SaveInformation(listadoAGrabar)
//    }
    private fun LeerDatosDePreferencias(){
        val listadoLeido = manejadorArchivo.ReadInformation()
        if(listadoLeido.first != null){
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText ( listadoLeido.first )
        editTextPassword.setText ( listadoLeido.second )
    }

//    private fun LeerDatosDePreferenciasEncrypt(){
//        val listadoLeido = manejadorArchivoEncrypt.ReadInformation()
//        if(listadoLeido.first != null){
//            checkBoxRecordarme.isChecked = true
//        }
//        editTextEmail.setText ( listadoLeido.first )
//        editTextPassword.setText ( listadoLeido.second )
//    }


    private fun ValidarDatosRequeridos():Boolean{
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()){
            editTextEmail.setError("Ingrese un email valido")
            editTextEmail.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            editTextEmail.setError("El email es obligatorio")
            editTextEmail.requestFocus()
            return false
        }
        if (clave.isEmpty()) {
            editTextPassword.setError("La clave es obligatoria")
            editTextPassword.requestFocus()
            return false
        }
        if (clave.length < 8) {
            editTextPassword.setError("La clave debe tener al menos 8 caracteres")
            editTextPassword.requestFocus()
            return false
        }
        return true
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

}