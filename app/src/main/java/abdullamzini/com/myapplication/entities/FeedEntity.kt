package abdullamzini.com.myapplication.entities

class FeedEntity {

    private var username: String = "Default"
    private lateinit var photoURL: String

    constructor()

    constructor(name: String, url:String) {
        username = name
        photoURL = url
    }

    fun getUsername(): String? {
        return username
    }

    fun getPhotoURL(): String? {
        return photoURL
    }

}