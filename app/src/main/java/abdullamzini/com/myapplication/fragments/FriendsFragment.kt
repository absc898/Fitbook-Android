package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.FriendsAdapter
import abdullamzini.com.myapplication.entities.FriendsEntity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_friends.*


class FriendsFragment : Fragment() {

    var friendAdapter: FriendsAdapter? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var query: Query

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        query = db.collection("users")
            .whereNotEqualTo("ID", auth.currentUser!!.uid.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(query, "All")
        radioGroup = view.findViewById(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {

            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                // checkedId is the RadioButton selected
                radioButton = view.findViewById(checkedId)
                if(radioButton.text == "All") {
                    query = db.collection("users")
                        .whereNotEqualTo("ID", auth.currentUser!!.uid.toString())
                    setUpRecyclerView(query, radioButton.text.toString())
                } else if(radioButton.text == "Requests") {
                    query = db.collection("users").document(auth.currentUser!!.uid.toString()).collection("friends")
                        .whereNotEqualTo("status", "Friends")
                    setUpRecyclerView(query, radioButton.text.toString())
                } else {
                    query = db.collection("users").document(auth.currentUser!!.uid.toString()).collection("friends")
                        .whereEqualTo("status", radioButton.text)
                    setUpRecyclerView(query, radioButton.text.toString())
                }

            }
        })

    }

    private fun setUpRecyclerView(query: Query, status:String) {
        // Query
        val options = FirestoreRecyclerOptions.Builder<FriendsEntity>()
            .setQuery(query, FriendsEntity::class.java)
            .setLifecycleOwner(this)
            .build()

        friendAdapter = FriendsAdapter(status, requireContext(), options)

        usersList.layoutManager = LinearLayoutManager(activity)
        usersList.adapter = friendAdapter


    }

}