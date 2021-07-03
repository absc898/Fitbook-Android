package abdullamzini.com.myapplication.entities

class CommentEntity {

    private var username: String = "Default"
    private lateinit var postDetails: String

    constructor(name: String, postDetails: String) {
        username = name
        this.postDetails = postDetails
    }

    fun getUsername(): String? {
        return username
    }

    fun getPostDetails(): String? {
        return postDetails
    }
}