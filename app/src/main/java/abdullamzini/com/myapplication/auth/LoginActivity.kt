package abdullamzini.com.myapplication.auth

import abdullamzini.com.myapplication.MainActivity
import abdullamzini.com.myapplication.R
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button
    private lateinit var createAccountText: TextView
    private lateinit var forgotPassText: TextView
    private lateinit var googleSignInClient: GoogleSignInClient

    val RC_SIGN_IN:Int=123

    public override fun onStart() {
        super.onStart()
        fAuth = FirebaseAuth.getInstance()

        if(fAuth.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameField = findViewById(R.id.loginEmail)
        passwordField = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        createAccountText = findViewById(R.id.newUsers)
        forgotPassText = findViewById(R.id.forgotPassword)
        googleButton = findViewById(R.id.googleButton)

        loginButton.setOnClickListener {
            val email: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            if(TextUtils.isEmpty(password) && TextUtils.isEmpty(email)) {  // check to see if the email or password field is blank
                usernameField?.error = "Please enter e-mail address"
                passwordField?.error = "Please enter password"
            }else if(TextUtils.isEmpty(password)) {
                passwordField?.error = "Please enter password"
            }else if(TextUtils.isEmpty(email)) {
                usernameField?.error = "Please enter e-mail address"
            } else {
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->
                        if(task.isSuccessful) {
                            val user = fAuth.currentUser
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        googleButton.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            signIn()
        }

        createAccountText.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GOOGLE", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLE", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("GOOGLE", "signInWithCredential:success")
                    val user = fAuth.currentUser
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("GOOGLE", "signInWithCredential:failure", task.exception)
                }
            }
    }
}