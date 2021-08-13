package com.example.proyect_smartmeter8vo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_home.*





class Home : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setup()
        session()
    }
    private fun session()
    {
        var prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email= prefs.getString("email",null)
        val provider= prefs.getString("provider",null)
        if (email != null && provider !=null)
        {
            showHome(email,ProviderType.valueOf(provider))
        }

    }


    private fun setup(){
        title="Autenticacion"
        btnRegistro.setOnClickListener {
        if(inputUser.text.isNotEmpty() && inputPassword.text.isNotEmpty())
        {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(inputUser.text.toString(),inputPassword.text.toString())
                .addOnCompleteListener {
                    showHome(it.result?.user?.email ?:"",ProviderType.BASIC)
                    showAlert()

                    if (!it.isSuccessful)
                    {
                        showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                    }
        }

        }
        }
        btnRegistro2.setOnClickListener {
            if(inputUser.text.isNotEmpty() && inputPassword.text.isNotEmpty())
            {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(inputUser.text.toString(),
                        inputPassword.text.toString())
                    .addOnCompleteListener {
                        showHome(it.result?.user?.email ?:"",ProviderType.BASIC)
                        showAlert()
                        if (!it.isSuccessful)
                        {
                            showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                        }
                    }
            }
        }
        btnGoogle.setOnClickListener {
            //Configuracion
            val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this,googleconf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }

    }

    private fun showAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Correcto")
        builder.setMessage("Usuario Creado Correctamente")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog= builder.create()
        dialog.show()
    }

    private fun showHome(email:String, provider: ProviderType)
    {
        val homeIntent = Intent(this,Principal::class.java).apply {
            putExtra("email",email)
            putExtra("provider", provider.name)

        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            try{
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if(account != null)
            {
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(){
                if (!it.isSuccessful)
                {
                    showHome(account.email ?:"",ProviderType.GOOGLE)

                }
            }

            }
        }catch (e:ApiException){
            //showAlert()

        }
        }
    }
}





