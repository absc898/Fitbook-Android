package abdullamzini.com.myapplication.fragments

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.adapters.GalleryAdapter
import abdullamzini.com.myapplication.auth.EditProfileActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var functions: FirebaseFunctions

    private lateinit var profilePic: ImageView
    private lateinit var numOfPosts: TextView
    private lateinit var numOfLikes: TextView
    private lateinit var numOfWorkouts: TextView
    private lateinit var editProfile: Button
    private lateinit var gridView: GridView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    var adapter: GalleryAdapter? = null
    private lateinit var feedback: TextView

    private lateinit var posts: ArrayList<*>
    private lateinit var likes: ArrayList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        functions = Firebase.functions
        collectionReference = db.collection("users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var docRef = db.collection("users").document(auth.currentUser?.uid.toString())

        docRef.addSnapshotListener { snapshot, e ->
            if(snapshot != null && view != null) {
                if(snapshot.data?.get("posts") != null) {
                    posts = snapshot.data?.get("posts") as ArrayList<*>
                }
                likes = snapshot.data?.get("likes") as ArrayList<*>
                val name = snapshot.data?.get("name")
                val phone = snapshot.data?.get("number")
                val email = snapshot.data?.get("email")
                numOfLikes = requireView().findViewById(R.id.numLikes)
                numOfPosts = requireView().findViewById(R.id.numPost)
                numOfWorkouts = requireView().findViewById(R.id.numWorkouts)
                profilePic = requireView().findViewById(R.id.profilePic)
                editProfile = requireView().findViewById(R.id.editButton)
                feedback = requireView().findViewById(R.id.feedBack)
                var url = ""

                numOfPosts.text = posts.size.toString()
                numOfLikes.text = likes.size.toString()
                profilePic.isDrawingCacheEnabled

                db.collection("users").document(auth.currentUser?.uid.toString()).collection("workouts")
                    .get()
                    .addOnSuccessListener { result ->
                        numOfWorkouts.text = result.size().toString()
                    }
                    .addOnFailureListener { exception ->
                        Log.w("WORKOUTS", "Error getting workouts.", exception)
                    }

                // TODO: REF - https://stackoverflow.com/questions/20743859/imageview-rounded-corners
                val pathReference: StorageReference =
                    FirebaseStorage.getInstance().reference.child("${auth.currentUser?.uid.toString()}/images/profilePic.jpg")
                pathReference.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext())
                        .load(it)
                        .into(profilePic)
                    url = it.toString()
                }.addOnFailureListener { it
                    Log.e("Image Download: ", it.message.toString())
                    profilePic.setImageResource(R.drawable.ic_baseline_person_24)
                }

                gridView = requireView().findViewById(R.id.gridOfImage)
                adapter = GalleryAdapter(requireContext(), posts)
                gridView.adapter = adapter


                editProfile.setOnClickListener{
                    val intent = Intent(requireContext(), EditProfileActivity::class.java)
                    intent.putExtra("editName",  name.toString())
                    intent.putExtra("editPhone", phone.toString())
                    intent.putExtra("editEmail", email.toString())
                    intent.putExtra("imageUri", url)
                    requireContext().startActivity(intent)
                }

                feedback.setOnClickListener {
                    val li = LayoutInflater.from(context)
                    val dialogView: View = li.inflate(R.layout.alert_feedback, null)
                    val builder = AlertDialog.Builder(requireContext())
                    val feedback = dialogView.findViewById(R.id.feedbackText) as TextView
                    builder.setView(dialogView)


                    builder.setPositiveButton("Yes") { dialog, which ->
                        if (feedback.text.isNotEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Sending feedback....",
                                Toast.LENGTH_SHORT
                            ).show()
                            val data = hashMapOf(
                                "feedback" to feedback.text.toString(),
                            )
                            functions.getHttpsCallable("sendFeedback")
                                .call(data)
                                .continueWith {
                                    Log.d(
                                        "Feedback",
                                        "Feedback sent"
                                    )
                                }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Feedback cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    builder.setNegativeButton("Cancel") { dialog, which ->
                        Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
                        }

                    builder.show()
                }
            } else {
                // Doc does not exist
            }
        }
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = requireView().findViewById(R.id.radioButtons)
        gridView = requireView().findViewById(R.id.gridOfImage)

        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                radioButton = view.findViewById(checkedId)
                if(radioButton.text == "posts") {
                    adapter = GalleryAdapter(requireContext(), posts)
                    gridView.adapter = adapter
                } else {
                    adapter = GalleryAdapter(requireContext(), likes)
                    gridView.adapter = adapter
                }

            }
        })
    }
}