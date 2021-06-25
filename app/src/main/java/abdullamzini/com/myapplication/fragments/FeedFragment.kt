package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.FeedAdapter
import abdullamzini.com.myapplication.entities.FeedEntity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment() {

    var siteAdapter: FeedAdapter? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        collectionReference = db.collection("posts")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        // Query
        val query : CollectionReference = collectionReference
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