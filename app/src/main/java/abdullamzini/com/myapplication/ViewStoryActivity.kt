package abdullamzini.com.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import jp.shts.android.storiesprogressview.StoriesProgressView


class ViewStoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var image: ImageView
    private var count = 0
    private lateinit var id: String
    private var photosIds = ArrayList<String>()

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_story)

        db = FirebaseFirestore.getInstance()
        storiesProgressView = findViewById(R.id.stories)
        image = findViewById(R.id.storyImage)
        id = intent.getStringExtra("id").toString()


        db.collection("users").document(id!!).collection("story")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    photosIds.add(document.data["imageId"].toString())
                }
                if(photosIds.size > 0) {
                    storiesProgressView.setStoriesCount(photosIds.size); // <- set stories
                    storiesProgressView.setStoryDuration(5000L); // <- set a story duration
                    storiesProgressView.setStoriesListener(this); // <- set listener
                    storiesProgressView.startStories(); // <- start progress

                    val pathReference: StorageReference =
                        FirebaseStorage.getInstance().reference.child("$id/story/${photosIds[count]}.jpg")
                    pathReference.downloadUrl.addOnSuccessListener {
                        Glide.with(this)
                            .load(it)
                            .into(image)
                    }.addOnFailureListener { it
                        Log.e("Image Download: ", it.message.toString())
                        image.setImageResource(R.drawable.ic_baseline_person_24)
                    }
                } else {
                    finish()
                }
            }
//            .addOnFailureListener { exception ->
//                //Log.w(TAG, "Error getting documents.", exception)
//            }


    }

    override fun onNext() {
        count++
        Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show()
        val pathReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("$id/story/${photosIds[count]}.jpg")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(image)
        }.addOnFailureListener { it
            Log.e("Image Download: ", it.message.toString())
            image.setImageResource(R.drawable.ic_baseline_person_24)
        }
    }

    override fun onPrev() {
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
    }

    override fun onComplete() {
        Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show()
        finish()
    }
}