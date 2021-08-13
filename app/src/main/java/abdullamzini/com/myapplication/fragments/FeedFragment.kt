package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.ViewStoryActivity
import abdullamzini.com.myapplication.adapters.FeedAdapter
import abdullamzini.com.myapplication.entities.FeedEntity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {

    var siteAdapter: FeedAdapter? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var query: Query

    private lateinit var myStory: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        query = db.collection("posts")
            .orderBy("date", Query.Direction.DESCENDING)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        myStory = view.findViewById(R.id.myStory)

        val pathReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("${auth.currentUser?.uid.toString()}/images/profilePic.jpg")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(requireContext())
                .load(it)
                .into(myStory)
        }.addOnFailureListener { it
            Log.e("Image Download: ", it.message.toString())
            myStory.setImageResource(R.drawable.ic_baseline_person_24)
        }

        myStory.setOnClickListener{
            val intent = Intent(context, ViewStoryActivity::class.java)
            intent.putExtra("id", auth.currentUser!!.uid.toString())
            startActivity(intent)
        }

        db.collection("users").document(auth.currentUser!!.uid.toString()).collection("friends")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val lin: LinearLayout = view.findViewById(R.id.storiesLayout)
                    val imageView = ImageView(lin.context)
                    val params = LinearLayout.LayoutParams(200, 200)
                    params.setMargins(100, 0 ,0, 0)

                    imageView.layoutParams = params
                    imageView.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.rounded)
                    val pathReference: StorageReference =
                        FirebaseStorage.getInstance().reference.child("${document.id}/images/profilePic.jpg")
                    pathReference.downloadUrl.addOnSuccessListener {
                        Glide.with(requireContext())
                            .load(it)
                            .into(imageView)
                    }.addOnFailureListener { it
                        Log.e("Image Download: ", it.message.toString())
                        imageView.setImageResource(R.drawable.ic_baseline_person_24)
                    }

                    imageView.setOnClickListener{
//                        Toast.makeText(context,
//                            "Select Image ${document.id}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, ViewStoryActivity::class.java)
                        intent.putExtra("id", document.data["ID"].toString())
                        startActivity(intent)

                    }
                    lin.addView(imageView)
                    //lin.removeView(imageView)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Story", "Error getting documents.", exception)
            }

    }

    private fun setUpRecyclerView() {

        val options = FirestoreRecyclerOptions.Builder<FeedEntity>()
            .setQuery(query, FeedEntity::class.java)
            .setLifecycleOwner(this)
            .build()

        siteAdapter = FeedAdapter(options)

        feedLists.layoutManager = LinearLayoutManager(activity)
        feedLists.adapter = siteAdapter


    }

    override fun onStart() {
        super.onStart()
        siteAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(siteAdapter != null) {
            siteAdapter!!.stopListening()
        }
    }

}