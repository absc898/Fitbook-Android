package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.CommentsActivity
import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.FeedEntity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.adapter_feed.view.*


class FeedAdapter(options: FirestoreRecyclerOptions<FeedEntity>) : FirestoreRecyclerAdapter<FeedEntity, FeedAdapter.FeedAdapterVH>(
    options
) {

    // TODO: Need to reference FIREBASE UI Cloudstore for below code

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapterVH {
        return FeedAdapterVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_feed,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FeedAdapterVH, position: Int, model: FeedEntity) {
        holder.username.text = model.getUsername()
        holder.postDetails.text = model.getPostDetails()
        //holder.postImage.setImageResource(R.drawable.example_post)
        val comments = model.getComments()
        val list = ArrayList<Map<String, String>>(comments)
        val ID = model.getID()
        val postUserID = model.getUserID()
        val photoID = model.getPhotoID()
        val likes = model.getUserLikesIds()

        holder.comments.setOnClickListener{
            val intent = Intent(holder.itemView.context, CommentsActivity::class.java)
            intent.putExtra("arraylist",  list)
            intent.putExtra("postID", ID)
            holder.itemView.context.startActivity(intent)
        }

        val pathReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("${postUserID}/posts/image@256_${photoID}.jpg")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(holder.postDetails.context)
                .load(it)
                .into(holder.postImage)
        }.addOnFailureListener {
            //Log.e("Image Download: ", it.message)
        }
        if (likes != null) {
            holder.likesButton.isLiked = Firebase.auth.currentUser!!.uid.toString() in likes

                holder.likesButton.setOnLikeListener(object : OnLikeListener {
                    override fun liked(likeButton: LikeButton) {
                        val like = hashMapOf(
                            "ID" to ID,
                            "status" to "true",
                        )
                        //holder.likesButton.isEnabled = false
                        Firebase.functions.getHttpsCallable("handleLikes")
                            .call(like)
                            .continueWith {
                            }

                    }

                    override fun unLiked(likeButton: LikeButton) {
                        val like = hashMapOf(
                            "ID" to ID,
                            "status" to "false",
                        )

                        Firebase.functions.getHttpsCallable("handleLikes")
                            .call(like)
                            .continueWith {
                                //holder.likesButton.isLiked = false
                            }

                    }
                })
        }
    }

    class FeedAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username = itemView.feedUsername
        var postImage = itemView.feedImage
        var postDetails = itemView.postDetails
        var likesButton = itemView.loveButton
        var comments = itemView.comments
        var cardSite = itemView.cardView
    }

}