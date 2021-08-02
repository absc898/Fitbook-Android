package abdullamzini.com.myapplication.entities

class FriendsEntity {

    private lateinit var status: String
    private lateinit var date: String
    private lateinit var ID: String
    private lateinit var name: String
    private lateinit var friends: ArrayList<String>

    constructor()

    constructor(status: String, date:String, id:String, name: String, friends: ArrayList<String>) {
        this.status = status
        this.date = date
        this.ID = id
        this.name = name
        this.friends = friends
    }

    fun getStatus(): String {
        return status
    }

    fun getDate(): String {
        return date
    }

    fun getID(): String {
        return ID
    }

    fun getName(): String {
        return name
    }

    fun getFriends(): ArrayList<String> {
        return friends
    }

}