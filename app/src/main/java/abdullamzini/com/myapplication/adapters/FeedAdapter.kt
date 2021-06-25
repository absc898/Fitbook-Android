package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.FeedEntity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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
        holder.postImage.setImageResource(R.drawable.example_post)

        //imageView.setImageURI(filePath)
    }

    class FeedAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username = itemView.feedUsername
        var postImage = itemView.feedImage
        var cardSite = itemView.cardView
    }

}