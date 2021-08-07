package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.WorkoutAdapter
import abdullamzini.com.myapplication.entities.WorkoutEntity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_fitness.*


class FitnessFragment : Fragment() {

    var workoutAdapter: WorkoutAdapter? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var collectionReference: CollectionReference
    private lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        query = db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("workouts")
            .orderBy("startTime", Query.Direction.DESCENDING)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fitness, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        val options = FirestoreRecyclerOptions.Builder<WorkoutEntity>()
            .setQuery(query, WorkoutEntity::class.java)
            .setLifecycleOwner(this)
            .build()

        workoutAdapter = WorkoutAdapter(options)

        workoutLists.layoutManager = LinearLayoutManager(activity)
        workoutLists.adapter = workoutAdapter
    }

    override fun onStart() {
        super.onStart()
        workoutAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(workoutAdapter != null) {
            workoutAdapter!!.stopListening()
        }
    }
}