package abdullamzini.com.myapplication.entities

class UserEntity {
    private var ID: String = "Default"
    private lateinit var email: String

    constructor(id: String, email: String) {
        ID = id
        this.email = email
    }

    fun getID(): String? {
        return ID
    }

    fun getEmail(): String? {
        return email
    }
}