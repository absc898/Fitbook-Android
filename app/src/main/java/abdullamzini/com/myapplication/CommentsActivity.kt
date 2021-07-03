package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.CommentAdapter
import abdullamzini.com.myapplication.entities.CommentEntity
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CommentsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    var arrayList: ArrayList<CommentEntity> = ArrayList()
    var adapter: CommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        //var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")

        val list = intent.getSerializableExtra("arraylist") as ArrayList<*>?

        listView = findViewById(R.id.commentListView)
        arrayList.add(CommentEntity("Abs", "My first post"))
        adapter = CommentAdapter(this, list as ArrayList<Map<String, String>>)
        listView.adapter = adapter

//        val adapter = ArrayAdapter(this, R.layout.view_comment, array)
//        val listView:ListView = findViewById(R.id.commentListView)
//        listView.adapter = adapter

    }
}