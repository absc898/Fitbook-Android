package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.GalleryAdapter
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailedFriendActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var functions: FirebaseFunctions

    private lateinit var postNum: TextView
    private lateinit var workoutNum: TextView
    private lateinit var profilePic: ImageView
    private lateinit var removeFriendButton: Button
    private lateinit var gridView: GridView
    var adapter: GalleryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_friend)

        db = FirebaseFirestore.getInstance()
        functions = Firebase.functions

        val friendId = intent.getStringExtra("friendId")
        postNum = findViewById(R.id.numPost)
        workoutNum = findViewById(R.id.numWorkouts)
        profilePic = findViewById(R.id.profilePic)
        removeFriendButton = findViewById(R.id.removeButton)
        gridView = findViewById(R.id.gridOfImage)

        val friendData = db.collection("users").document(friendId.toString())
        friendData.get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val data = document.data
                    val name = data?.get("name")
                    val posts = data?.get("posts") as ArrayList<String>
                    postNum.text = posts.size.toString()

                    adapter = GalleryAdapter(this, posts, friendId.toString())
                    gridView.adapter = adapter

                    db.collection("users").document(friendId!!).collection("workouts")
                        .get()
                        .addOnSuccessListener { result ->
                            workoutNum.text = result.size().toString()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("WORKOUTS", "Error getting workouts.", exception)
                        }

                    val pathReference: StorageReference =
                        FirebaseStorage.getInstance().reference.child("$friendId/images/profilePic.jpg")
                    pathReference.downloadUrl.addOnSuccessListener {
                        Glide.with(this)
                            .load(it)
                            .into(profilePic)
                    }.addOnFailureListener { it
                        Log.e("Image Download: ", it.message.toString())
                    }

                    Log.i("FRIEND", "getting Friend")

                } else {
                    //does not exist
                }
            }
            .addOnFailureListener{ exception ->
                Log.d("FRIEND", "getting Friend failed with ", exception)
            }

        removeFriendButton.setOnClickListener{
            val data = hashMapOf(
                "friendId" to friendId,
                "status" to "decline",

                )
            functions.getHttpsCallable("updateFriend")
                .call(data)
                .continueWith {
                    Log.d("FRIEND", "Updating Friend")
                    Toast.makeText(this, "Removed Friend Request",
                        Toast.LENGTH_SHORT).show()
                    finish()

                }
        }

    }
}