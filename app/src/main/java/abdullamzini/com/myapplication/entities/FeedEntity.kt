package abdullamzini.com.myapplication.entities

class FeedEntity {

    private var username: String = "Default"
    private lateinit var photoID: String
    private lateinit var postDetails: String
    private lateinit var userID: String
    private lateinit var ID: String
    private lateinit var userLikesIds: List<String>

    constructor()

    constructor(name: String, urlID:String, postDetails: String, userID: String, ID: String, userLikesIds: List<String>) {
        username = name
        photoID = urlID
        this.postDetails = postDetails
        this.userID = userID
        this.ID = ID
        this.userLikesIds = userLikesIds
    }

    fun getUsername(): String? {
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
}