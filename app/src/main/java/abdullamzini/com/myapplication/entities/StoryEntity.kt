package abdullamzini.com.myapplicatioxn.entities

class StoryEntity {

    private lateinit var imageUrl: String
    private lateinit var timeStart: String
    private lateinit var timeEnd: String
    private lateinit var storyId: String
    private lateinit var userId: String

    constructor()

    constructor(imageUrl: String, timeStart: String, timeEnd: String, storyId: String, userId: String) {
        this.imageUrl = imageUrl
        this.timeStart = timeStart
        this.timeEnd = timeEnd
        this.storyId = storyId
        this.userId = userId
    }

    fun getImageUrl(): String {
        return imageUrl
    }

    fun getTimeStart(): String {
        return timeStart
    }

    fun getTimeEnd(): String {
        return timeEnd
    }

    fun getStoryId(): String {
        return storyId
    }

    fun getUserId(): String {
        return userId
    }

}