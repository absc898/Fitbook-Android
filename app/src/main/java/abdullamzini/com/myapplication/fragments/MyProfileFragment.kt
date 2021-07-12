package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.GalleryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MyProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

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
                var currentDoc = snapshot.data;
                posts = snapshot.data?.get("posts") as ArrayList<*>
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