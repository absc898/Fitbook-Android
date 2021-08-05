package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.DetailedFriendActivity
import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.FriendsEntity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.adapter_friends.view.*

class FriendsAdapter (private val status: String, private val context: Context, options: FirestoreRecyclerOptions<FriendsEntity>) : FirestoreRecyclerAdapter<FriendsEntity, FriendsAdapter.FriendsAdapterVH>(
    options
) {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var functions: FirebaseFunctions = Firebase.functions

    // TODO: Need to reference FIREBASE UI Cloudstore for below code

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsAdapter.FriendsAdapterVH {
        return FriendsAdapter.FriendsAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.adapter_friends, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FriendsAdapterVH, position: Int, model: FriendsEntity) {
        if(auth.currentUser!!.uid != model.getID() && model.getName() != null) {
            if(status != "All") {
                holder.request.isEnabled = false
                holder.request.visibility = View.GONE
                if(status == "Pending") {
                    holder.accept.visibility = View.VISIBLE
                    holder.decline.visibility = View.VISIBLE
                }

            } else {
                val friends = model.getFriends()
                if(friends.contains(auth.currentUser!!.uid.toString())) {
                    holder.request.isEnabled = false
                }
            }

            holder.friendsName.text = model.getName()

            val pathReference: StorageReference =
                FirebaseStorage.getInstance().reference.child("${model.getID()}/images/profilePic.jpg")
            pathReference.downloadUrl.addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .into(holder.friendsImage)
            }.addOnFailureListener { it
                Log.e("Image Download: ", it.message.toString())
                holder.friendsImage.setImageResource(R.drawable.ic_baseline_person_24)
            }

            holder.request.setOnClickListener{
                val data = hashMapOf(
                    "friendId" to model.getID(),
                    "friendName" to model.getName(),
                    "myName" to auth.currentUser!!.displayName.toString()

                )
                functions.getHttpsCallable("addFriend")
                    .call(data)
                    .continueWith {
                            Log.d("FRIEND", "Added Friend")
                        Toast.makeText(context, "Friend Request Sent",
                            Toast.LENGTH_SHORT).show()

                    }
            }

            holder.decline.setOnClickListener{
                val data = hashMapOf(
                    "friendId" to model.getID(),
                    "status" to "decline",

                )
                functions.getHttpsCallable("updateFriend")
                    .call(data)
                    .continueWith {
                        Log.d("FRIEND", "Updating Friend")
                        Toast.makeText(context, "Declined Friend Request",
                            Toast.LENGTH_SHORT).show()

                    }
            }

            holder.accept.setOnClickListener{
                val data = hashMapOf(
                    "friendId" to model.getID(),
                    "status" to "accept",
                    )
                functions.getHttpsCallable("updateFriend")
                    .call(data)
                    .continueWith {
                        Log.d("FRIEND", "Updating Friend")
                        Toast.makeText(context, "Accepting Friend Request",
                            Toast.LENGTH_SHORT).show()

                    }
            }

            holder.cardView.setOnClickListener{
                if(status == "Friends") {
                    val intent = Intent(context, DetailedFriendActivity::class.java)
                    intent.putExtra("friendId", model.getID())
                    context.startActivity(intent)
                    Toast.makeText(context, "Loading Friend...",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    class FriendsAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var friendsImage = itemView.FriendsPhoto
        var friendsName = itemView.FriendsName
        var cardView = itemView.cardView
        var request = itemView.requestButton
        var decline = itemView.declineView
        var accept = itemView.acceptView
    }

}