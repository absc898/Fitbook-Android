package abdullamzini.com.myapplication.entities

class WorkoutEntity {
    private var duration: Long = 0
    private lateinit var endTime: String
    private lateinit var startTime: String
    private lateinit var type: String
    private lateinit var workoutID: String
    private lateinit var workoutName: String
    private lateinit var description: String
    private lateinit var movements: ArrayList<Map<String, String>>

    constructor()

    constructor(length: Long, endTime:String, startTime: String, type: String, ID: String, name: String, des: String, movements: ArrayList<Map<String, String>>) {
        duration = length
        this.endTime = endTime
        this.startTime = startTime
        this.type = type
        this.workoutID = ID
        this.workoutName = name
        this.movements = movements
        this.description = des
    }

    fun getDuration(): Long {
        return duration
    }

    fun getEndTime(): String? {
        return endTime
    }

    fun getStartTime(): String? {
        return startTime
    }

    fun getType(): String? {
        return type
    }
    fun getWorkoutID(): String? {
        return workoutID
    }

    fun getWorkoutName() : String? {
        return workoutName
    }

    fun getDescription() : String? {
        return description
    }

    fun getMovements() : ArrayList<Map<String, String>> {
        return movements
    }
}