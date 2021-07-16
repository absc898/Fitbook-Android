package abdullamzini.com.myapplication.entities

class FeedEntity {

    private var username: String = "Default"
    private lateinit var photoID: String
    private lateinit var postDetails: String
    private lateinit var userID: String
    private lateinit var ID: String
    private lateinit var userLikesIds: List<String>
    private lateinit var comments: ArrayList<Map<String, String>>

    constructor()

    constructor(name: String, urlID:String, postDetails: String, userID: String, ID: String, userLikesIds: ArrayList<String>,
                comments: ArrayList<Map<String, String>>) {
        username = name
        photoID = urlID
        this.postDetails = postDetails
        this.userID = userID
        this.ID = ID
        this.userLikesIds = userLikesIds
        this.comments = comments
    }

    fun getUsername(): String {
        return username
    }

    fun getPhotoID(): String? {
        return photoID
    }

    fun getPostDetails(): String? {
        return postDetails
    }

    fun getUserID(): String? {
        return userID
    }
    fun getID(): String? {
        return ID
    }

    fun getUserLikesIds() : List<String>? {
        return userLikesIds
    }

    fun getComments() : ArrayList<Map<String, String>> {
        return comments
    }
}