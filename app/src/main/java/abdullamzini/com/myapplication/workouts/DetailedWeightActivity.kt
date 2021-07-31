package abdullamzini.com.myapplication.workouts

import abdullamzini.com.myapplication.R
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.DataDeleteRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class DetailedWeightActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var startDate: TextView
    private lateinit var descriptionView: TextView
    private lateinit var weightList: TableLayout
    private lateinit var deleteWorkout: Button
    private lateinit var home: EditText
    private lateinit var away: EditText
    private lateinit var homeView: TextView
    private lateinit var awayView: TextView

    private lateinit var functions: FirebaseFunctions
    private lateinit var fitnessOptions: FitnessOptions

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weight)

        functions = Firebase.functions

        title = findViewById(R.id.workoutTitle)
        startDate = findViewById(R.id.workoutDate)
        descriptionView = findViewById(R.id.workoutDescription)
        weightList = findViewById(R.id.weightList)
        deleteWorkout = findViewById(R.id.deleteButton)
        home = findViewById(R.id.homePoints)
        away = findViewById(R.id.awayPoints)
        homeView = findViewById(R.id.homeView)
        awayView = findViewById(R.id.awayView)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")
        val description = intent.getStringExtra("description")
        val startTime = intent.getStringExtra("startTime")
        val endTime = intent.getStringExtra("endTime")!!.toLong()
        val startTimeSeconds = intent.getStringExtra("startTimeSeconds")!!.toLong()
        val type = intent.getStringExtra("type")

        title.text = name
        startDate.text = startTime
        descriptionView.text = description

        fitnessOptions = FitnessOptions.builder()
            .accessActivitySessions(FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .build()

        val arl = intent.getSerializableExtra("movements") as ArrayList<*>?

        if(type != FitnessActivities.BASKETBALL) {
            if (arl != null) {
                for(item in arl) {
                    val tempMap: HashMap<String, String> = item as HashMap<String, String>
                    val row: TableRow = LayoutInflater.from(this)
                        .inflate(R.layout.table_list_temp, null) as TableRow
                    (row.findViewById(R.id.attrib_name) as EditText).hint = tempMap["name"]
                    (row.findViewById(R.id.attrib_set) as EditText).hint = tempMap["sets"]
                    (row.findViewById(R.id.attrib_reps) as EditText).hint = tempMap["reps"]
                    (row.findViewById(R.id.attrib_wight) as EditText).hint = tempMap["weight"]
                    weightList.addView(row)
                }
            }
        } else {
            if (arl != null) {
                for(item in arl) {
                    val tempMap: HashMap<String, String> = item as HashMap<String, String>
                    home.setText(tempMap["home"])
                    away.setText(tempMap["away"])
                    home.visibility = View.VISIBLE
                    away.visibility = View.VISIBLE
                    homeView.visibility = View.VISIBLE
                    awayView.visibility = View.VISIBLE
                }
            }
        }


        val session = Session.Builder()
            .setName("Fitbook " + name)
            .setIdentifier(id)
            .setDescription(description)
            .setActivity(type)
            .setStartTime(startTimeSeconds, TimeUnit.SECONDS)
            .setEndTime(endTime, TimeUnit.SECONDS)
            .build()

        val endTime1 = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime1 = endTime1.minusWeeks(10)

        deleteWorkout.setOnClickListener{
            val request = DataDeleteRequest.Builder()
                .setTimeInterval(startTime1.toEpochSecond(), endTime1.toEpochSecond(), TimeUnit.SECONDS)
                .addSession(session)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .build()

            Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .deleteData(request)
                .addOnSuccessListener { response ->
                    Log.i("TAG", "Successfully deleted today's sessions")
                    val data = hashMapOf(
                        "id" to intent.getStringExtra("id"),
                    )
                    functions.getHttpsCallable("deleteWorkout")
                        .call(data)
                        .continueWith {
                            Log.d("WORKOUT", "Deleted Workout!")
                            finish()
                        }

                }            .addOnFailureListener { e ->
                    Log.w("TAG","Failed to dellete session", e)
                }
        }
    }
}