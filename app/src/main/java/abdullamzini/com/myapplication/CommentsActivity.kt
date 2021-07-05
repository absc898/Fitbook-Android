package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.CommentAdapter
import abdullamzini.com.myapplication.entities.CommentEntity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class CommentsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    private lateinit var listView: ListView
    private lateinit var postComment: Button
    private lateinit var commentText: EditText
    var arrayList: ArrayList<CommentEntity> = ArrayList()
    var adapter: CommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        functions = Firebase.functions
        auth = FirebaseAuth.getInstance()
        postComment = findViewById(R.id.postButton)
        commentText = findViewById(R.id.commentText)

        val list = intent.getSerializableExtra("arraylist") as ArrayList<*>?
        val postID = intent.getStringExtra("postID")

        listView = findViewById(R.id.commentListView)
        arrayList.add(CommentEntity("Abs", "My first post"))
        adapter = CommentAdapter(this, list as ArrayList<Map<String, String>>)
        listView.adapter = adapter

        postComment.setOnClickListener{
            val comment = commentText.text

            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(baseContext, "Please add a comment",
                    Toast.LENGTH_SHORT).show()
            } else {
                val user = auth.currentUser
                val data = hashMapOf(
                    "postID" to postID,
                    "userID" to (user?.uid ?: "Unknown"),
                    "username" to (user?.displayName ?: "Unknown"),
                    "postDetails" to comment.toString(),
                    "timeStamp" to now().toString()
                )

                functions.getHttpsCallable("addComment")
                    .call(data)
                    .continueWith {
                        finish()
                    }
            }

        }
    }
}