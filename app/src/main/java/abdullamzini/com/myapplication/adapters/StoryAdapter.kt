package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class StoryAdapter(private val context: Context) : BaseAdapter() {

    private lateinit var comment: TextView
    private lateinit var userImage: ImageView
    private lateinit var dateAndTime: TextView
    private lateinit var mainLayout: ConstraintLayout


    override fun getCount(): Int {
        return 1
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.story_add_photo, parent, false)

        comment = convertView.findViewById(R.id.commentDetails)


        return convertView
    }
}
