package abdullamzini.com.myapplication.Auth

import abdullamzini.com.myapplication.MainActivity
import abdullamzini.com.myapplication.R
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountText: TextView
    private lateinit var forgotPassText: TextView

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

        createAccountText.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

    }
}