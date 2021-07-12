package abdullamzini.com.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    private lateinit var postDetails: EditText
    private lateinit var addPhoto: Button
    private lateinit var postImage: ImageView

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        auth = FirebaseAuth.getInstance()
        functions = Firebase.functions
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        postDetails = findViewById(R.id.postDetails)
        addPhoto = findViewById(R.id.postAddPhoto)
        postImage = findViewById(R.id.postImage)


        addPhoto.setOnClickListener{
            val options = arrayOf("Gallery", "Camera", "Workout")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Photo upload option")
                .setItems(options) { dialog, which ->
                    if(which == 0) {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select a building site Image"),
                            PICK_IMAGE_REQUEST
                        )
                    } else if(which == 1) { // TODO: https://developer.android.com/training/camera/photobasics
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            builder.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.upload -> {
            if(filePath != null){
                preparePost()
            } else {
                Toast.makeText(baseContext, "Please select an photo for your post",
                    Toast.LENGTH_SHORT).show()
            }

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data?.data
            postImage.setImageURI(filePath)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            filePath = getImageUri(this, imageBitmap)
            postImage.setImageBitmap(imageBitmap)
        }
    }

    // TODO: https://stackoverflow.com/questions/26059748/is-there-a-way-to-get-uri-of-bitmap-without-saving-it-to-sdcard
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun preparePost() {
        val postDetails: String = postDetails.text.toString()
        val user = auth.currentUser
        val uniqueImageIDName = UUID.randomUUID().toString()
        val storageReference = storage.reference

        val post = hashMapOf(
            "username" to (user?.displayName ?: "unknown"),
            "postDetails" to postDetails,
            "photoID" to uniqueImageIDName,
            "date" to now().toString()
        )

        val imageRef: StorageReference = storageReference.child("${user?.uid}/posts/$uniqueImageIDName.jpg")
        imageRef.putFile(filePath!!).addOnSuccessListener {
            functions.getHttpsCallable("addPost")
                .call(post)
                .continueWith {
                    Toast.makeText(baseContext, "Image upload",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

    }
}