package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CommentAdapter(private val context: Context, private val arrayList: ArrayList<Map<String, String>>) : BaseAdapter() {
    private lateinit var comment: TextView


    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)
        comment = convertView.findViewById(R.id.commentDetails)
        val obj = arrayList[position]
        comment.text = obj["postDetails"]

        return convertView
    }
}