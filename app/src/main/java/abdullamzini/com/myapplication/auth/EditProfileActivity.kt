package abdullamzini.com.myapplication.auth

import abdullamzini.com.myapplication.R
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    private lateinit var editName: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editProfilePic: ImageView
    private lateinit var saveButton: Button
    private lateinit var passwordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        functions = Firebase.functions

        val editTextName = intent.getStringExtra("editName")
        val url = Uri.parse(intent.getStringExtra("imageUri"))

        editName = findViewById(R.id.editName)
        editPhone = findViewById(R.id.editPhone)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        editProfilePic = findViewById(R.id.editImage)
        saveButton = findViewById(R.id.saveButton)
        passwordButton = findViewById(R.id.passwordButton)

        editName.setText(editTextName)
        editPhone.setText(intent.getStringExtra("editPhone"))
        editEmail.setText(intent.getStringExtra("editEmail"))

        Glide.with(this)
            .load(url)
            .into(editProfilePic)


        saveButton.setOnClickListener {
            val user = auth.currentUser
            val profileUpdate = userProfileChangeRequest {
                displayName = editName.text.toString()
            }
            user!!.updateProfile(profileUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val updatedUser = auth.currentUser
                        val data = hashMapOf(
                            "name" to (updatedUser?.displayName ?: "empty"),
                            "url" to user.photoUrl.toString(),
                            "phone" to editPhone.text.toString(),
                        )
                        functions.getHttpsCallable("updateUser")
                            .call(data)
                            .continueWith {
                                if (updatedUser != null) {
                                    Log.d(
                                        "UpdateProfile",
                                        "User profile updated with name ${updatedUser.displayName}"
                                    )
                                    finish()
                                }
                            }
                    }
                }
        }

        passwordButton.setOnClickListener{
            val user = auth.currentUser
            user!!.updatePassword(editPassword.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("PASSWORD", "Password has been change")
                        finish()
                    }
                }
        }

    }
}