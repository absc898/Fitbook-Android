package abdullamzini.com.myapplication.entities

class FeedEntity {

    private var username: String = "Default"
    private lateinit var photoID: String
    private lateinit var postDetails: String
    private lateinit var userID: String

    constructor()

    constructor(name: String, urlID:String, postDetails: String, userID: String) {
        username = name
        photoID = urlID
        this.postDetails = postDetails
        this.userID = userID
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

}