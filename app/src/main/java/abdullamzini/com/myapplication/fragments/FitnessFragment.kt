package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.WorkoutAdapter
import abdullamzini.com.myapplication.entities.WorkoutEntity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_fitness.*
import java.util.*


class FitnessFragment : Fragment() {

    private var workoutAdapter: WorkoutAdapter? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var query: Query

    private lateinit var workoutOptions: Spinner

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
        setUpRecyclerView(query)

        workoutOptions = view.findViewById(R.id.filters)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.workout_filter,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            workoutOptions.adapter = adapter
        }

        workoutOptions?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = workoutOptions.selectedItem.toString()
                if(selectedItem == "All") {
                    setUpRecyclerView(query.orderBy("startTime", Query.Direction.DESCENDING))

                } else {
                    setUpRecyclerView(query.whereEqualTo("type", selectedItem.lowercase(Locale.getDefault())))
                }
            }

        }
    }

    private fun setUpRecyclerView(query: Query) {
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