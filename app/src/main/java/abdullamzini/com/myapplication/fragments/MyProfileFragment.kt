package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.GalleryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MyProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    private lateinit var profilePic: ImageView
    private lateinit var numOfPosts: TextView
    private lateinit var numOfLikes: TextView
    private lateinit var gridView: GridView
    var adapter: GalleryAdapter? = null

    private lateinit var posts: ArrayList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection("users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var docRef = db.collection("users").document(auth.currentUser?.uid.toString())

        docRef.addSnapshotListener { snapshot, e ->
            if(snapshot != null) {
                posts = snapshot.data?.get("posts") as ArrayList<*>
                val likes = snapshot.data?.get("likes") as ArrayList<*>
                numOfLikes = requireView().findViewById(R.id.numLikes)
                numOfPosts = requireView().findViewById(R.id.numPost)
                profilePic = requireView().findViewById(R.id.profilePic)

                numOfPosts.text = posts.size.toString()
                numOfLikes.text = likes.size.toString()

                val pathReference: StorageReference =
                    FirebaseStorage.getInstance().reference.child("${auth.currentUser?.uid.toString()}/images/profilePic.jpg")
                pathReference.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext())
                        .load(it)
                        .into(profilePic)
                }.addOnFailureListener {
                    //Log.e("Image Download: ", it.message)
                }

                gridView = requireView().findViewById(R.id.gridOfImage)
                adapter = GalleryAdapter(requireContext(), posts, auth.currentUser?.uid.toString())
                gridView.adapter = adapter
            } else {
                // Doc does not exist
            }
        }
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}