package abdullamzini.com.myapplication.adapters


import abdullamzini.com.myapplication.R
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class GalleryAdapter(private val context: Context, private val arrayList: ArrayList<*>, private val uid: String) : BaseAdapter() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // TODO: Might want to add a the constructor to pull the posts data for when a user selects it

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any? {
        Toast.makeText(context, "Get Item",
            Toast.LENGTH_SHORT).show()
        return null
    }

    override fun getItemId(position: Int): Long {
        Toast.makeText(context, "getItemID: ${arrayList[position]}",
            Toast.LENGTH_SHORT).show()
//        val intent = Intent(mContext, CreateAccountActivity::class.java)
//        mContext?.startActivity(intent)

        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val imageView: ImageView = ImageView(context)
        imageView.layoutParams = AbsListView.LayoutParams(300, 300)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setPadding(60, 0, 0, 0)
        db.collection("posts").document(arrayList[position].toString()).get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    var imageID =  document.data?.get("photoID").toString()
                    val pathReference: StorageReference =
                        FirebaseStorage.getInstance().reference.child("${uid}/posts/${imageID}.jpg")
                    pathReference.downloadUrl.addOnSuccessListener {
                        Glide.with(context)
                            .load(it)
                            .into(imageView)
                    }.addOnFailureListener {
                        //Log.e("Image Download: ", it.message)
                    }
                } else {
                    Toast.makeText(context, "Failed to get post: ${arrayList[position]}",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("GALLERY", "Failed to get post data", exception)
            }



//        if (convertView == null) {
//            // if it's not recycled, initialize some attributes
//            imageView = ImageView(context)
//            imageView.layoutParams = AbsListView.LayoutParams(300, 300)
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.setPadding(0, 0, 0, 0)
//        } else {
//            imageView = convertView as ImageView
//        }
//        imageView.setImageResource(mThumbIds[position])
        return imageView
    }

    // references to our images
    private var mThumbIds = arrayOf<Int>(
        abdullamzini.com.myapplication.R.drawable.example_post, abdullamzini.com.myapplication.R.drawable.example_post,
        R.drawable.example_post, R.drawable.example_post,
        R.drawable.example_post, R.drawable.example_post,
        R.drawable.example_post, R.drawable.example_post,
    )
}