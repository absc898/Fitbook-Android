package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.FeedEntity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
        holder.postImage.setImageResource(R.drawable.example_post)

        val userID = model.getUserID()
        val photoID = model.getPhotoID()
        val pathReference: StorageReference =
            FirebaseStorage.getInstance().reference.child("${userID}/posts/${photoID}.jpg")
        pathReference.downloadUrl.addOnSuccessListener {
            Glide.with(holder.postDetails.context)
                .load(it)
                .into(holder.postImage)
        }.addOnFailureListener {
            //Log.e("Image Download: ", it.message)
        }
    }

    class FeedAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username = itemView.feedUsername
        var postImage = itemView.feedImage
        var postDetails = itemView.postDetails
        var cardSite = itemView.cardView
    }

}