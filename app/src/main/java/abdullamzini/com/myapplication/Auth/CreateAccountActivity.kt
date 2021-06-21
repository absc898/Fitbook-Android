package abdullamzini.com.myapplication.Auth

import abdullamzini.com.myapplication.R
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var displayName: EditText
    private lateinit var phone: EditText
    private lateinit var submitBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        functions = Firebase.functions

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        displayName = findViewById(R.id.editTextTextPersonName)
        phone = findViewById(R.id.editTextPhone)
        submitBtn = findViewById(R.id.submitBtn)


        submitBtn.setOnClickListener {
            val name: String = displayName.text.toString()
            val mail: String = email.text.toString()
            val pass: String = password.text.toString()
            val phone: String = phone.text.toString()

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(mail) && TextUtils.isEmpty((pass))) { // check to see if the email or password field is blank
                email.error = "Please enter e-mail address"
                displayName.error = "Please enter drivers name"
                password.error = "Please enter drivers name"
            }else if (TextUtils.isEmpty(name)){
                displayName.error = "Please enter drivers name"
            }else if (!isEmailValid(mail)){
                email.error = "Please enter e-mail address"
            }else {
                createAccount(name, mail, pass, phone)
            }
        }
    }

    private fun createAccount(name: String, mail: String, pass: String, phone: String) {

        auth.createUserWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                        photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val updatedUser = auth.currentUser
                                val data = hashMapOf(
                                    "name" to (updatedUser?.displayName ?: "empty"),
                                    "url" to "https://example.com/jane-q-user/profile.jpg",
                                    "phone" to phone,
                                    "push" to true
                                )
                                functions.getHttpsCallable("updateUser")
                                    .call(data)
                                if (updatedUser != null) {
                                    Log.d("UpdateProfile", "User profile updated with name ${updatedUser.displayName}")
                                }
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }
}