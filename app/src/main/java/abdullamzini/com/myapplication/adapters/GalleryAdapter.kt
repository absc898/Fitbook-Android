package abdullamzini.com.myapplication.adapters


import abdullamzini.com.myapplication.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast


class GalleryAdapter(private val context: Context, private val arrayList: ArrayList<*>) : BaseAdapter() {

    override fun getCount(): Int {
        return mThumbIds.size
    }

    override fun getItem(position: Int): Any? {
        Toast.makeText(context, "Get Item",
            Toast.LENGTH_SHORT).show()
        return null
    }

    override fun getItemId(position: Int): Long {
        Toast.makeText(context, "getItemID: $position",
            Toast.LENGTH_SHORT).show()
//        val intent = Intent(mContext, CreateAccountActivity::class.java)
//        mContext?.startActivity(intent)

        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val imageView: ImageView
        var newAL = arrayList
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(context)
            imageView.setLayoutParams(AbsListView.LayoutParams(300, 300))
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
            imageView.setPadding(0, 0, 0, 0)
        } else {
            imageView = convertView as ImageView
        }
        imageView.setImageResource(mThumbIds.get(position))
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