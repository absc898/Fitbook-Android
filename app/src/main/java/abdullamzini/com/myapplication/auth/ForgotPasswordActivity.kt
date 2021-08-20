package abdullamzini.com.myapplication.auth

import abdullamzini.com.myapplication.R
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailAddress: EditText
    private lateinit var send: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        emailAddress = findViewById(R.id.emailAddress)
        send = findViewById(R.id.sendButton)

        send.setOnClickListener{
            Firebase.auth.sendPasswordResetEmail(emailAddress.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FORGOTPASS", "Email sent.")
                        Toast.makeText(baseContext, "Reset link sent",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Email failed to send",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}