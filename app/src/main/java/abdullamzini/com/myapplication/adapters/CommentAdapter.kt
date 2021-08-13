package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalTime

class CommentAdapter(private val context: Context, private val arrayList: ArrayList<Map<String, String>>) : BaseAdapter() {

    private lateinit var comment: TextView
    private lateinit var userImage: ImageView
    private lateinit var dateAndTime: TextView
    private lateinit var mainLayout: ConstraintLayout


    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)

        comment = convertView.findViewById(R.id.commentDetails)
        userImage = convertView.findViewById(R.id.commentImage)
        dateAndTime = convertView.findViewById(R.id.dateAndTime)
        mainLayout = convertView.findViewById(R.id.mainLay)

        val obj = arrayList[position]

        val pathReference: StorageReference = FirebaseStorage.getInstance().reference.child("${obj["userID"]}/images/profilePic.jpg")
        pathReference.downloadUrl.addOnSuccessListener {
            val imgView = convertView.findViewById<ImageView>(R.id.commentImage)
            Glide
                .with(context)
                .load(it)
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(imgView)
        }.addOnFailureListener {
            //Log.e("Image Download: ")
            userImage.setImageResource(R.drawable.ic_baseline_person_24)
        }

        comment.text = obj["postDetails"]

        val seconds = obj["timeStamp"]?.toLong()
        val cal = (seconds ?: 0) * 1000L
        val date = DateFormat.format("dd-MM-yyyy",cal).toString()
        val time = LocalTime.MIN.plusSeconds(seconds!!).toString();
        dateAndTime.text = "$date $time"

        return convertView
    }
}