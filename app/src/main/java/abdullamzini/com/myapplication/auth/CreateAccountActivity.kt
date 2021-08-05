package abdullamzini.com.myapplication.auth

import abdullamzini.com.myapplication.MainActivity
import abdullamzini.com.myapplication.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.regex.Matcher
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var displayName: EditText
    private lateinit var phone: EditText
    private lateinit var submitBtn: Button
    private lateinit var imageBtn: Button
    private lateinit var imageView: ImageView

    // Image select var
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        functions = Firebase.functions
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        displayName = findViewById(R.id.editTextTextPersonName)
        phone = findViewById(R.id.editTextPhone)
        submitBtn = findViewById(R.id.submitBtn)
        imageBtn = findViewById(R.id.imageSelectButton)
        imageView = findViewById(R.id.imageView)

        imageBtn.setOnClickListener {
            selectImage()
        }

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

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select a building site Image"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data?.data
            imageView.setImageURI(filePath)
        }
    }

    private fun createAccount(name: String, mail: String, pass: String, phone: String) {

        auth.createUserWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val user = auth.currentUser
                    if(filePath != null) {
                        val imageRef: StorageReference =
                            storageReference.child("${user?.uid}/images/profilePic.jpg")
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                            photoUri = Uri.parse(filePath.toString())
                        }
                        imageRef.putFile(filePath!!).addOnSuccessListener {
                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val updatedUser = auth.currentUser
                                        val data = hashMapOf(
                                            "name" to (updatedUser?.displayName ?: "empty"),
                                            "url" to filePath.toString(),
                                            "phone" to phone,
                                            "push" to true
                                        )
                                        functions.getHttpsCallable("updateUser")
                                            .call(data)
                                            .continueWith {
                                                if (updatedUser != null) {
                                                    Log.d("UpdateProfile",
                                                        "User profile updated with name ${updatedUser.displayName}")
                                                    startActivity(Intent(this, MainActivity::class.java))
                                                    finish()
                                                }
                                            }
                                    }
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