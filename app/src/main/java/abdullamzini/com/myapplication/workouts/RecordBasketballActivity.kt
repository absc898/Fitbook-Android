package abdullamzini.com.myapplication.workouts

import abdullamzini.com.myapplication.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class RecordBasketballActivity : AppCompatActivity() {

    private lateinit var timer: Chronometer
    private lateinit var startTimer: Button
    private lateinit var pauseTime: Button
    private lateinit var finishedButton: Button
    private lateinit var home: EditText
    private lateinit var away: EditText
    private lateinit var loadingBar: ProgressBar

    private lateinit var functions: FirebaseFunctions
    private lateinit var fitnessOptions: FitnessOptions

    var GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 347

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_basketball)

        var stopTime: Long = 0
        var startTime: Long = 0

        functions = Firebase.functions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE)
        }

        startTimer = findViewById(R.id.startTimeButton)
        pauseTime = findViewById(R.id.pauseButton)
        timer = findViewById(R.id.textViewStopWatch)
        finishedButton = findViewById(R.id.doneButton)
        home = findViewById(R.id.homePoints)
        away = findViewById(R.id.awayPoints)
        loadingBar = findViewById(R.id.loadingBar)

        fitnessOptions = FitnessOptions.builder()
            .accessActivitySessions(FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY,FitnessOptions.ACCESS_WRITE)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions)
        }

        //TODO: REF - https://www.youtube.com/watch?v=6AZH9QycL0A

        startTimer.setOnClickListener{
            startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
            timer.base = SystemClock.elapsedRealtime() + stopTime
            timer.start()
            startTimer.visibility = View.GONE
            pauseTime.visibility = View.VISIBLE
        }

        pauseTime.setOnClickListener{
            stopTime = timer.base - SystemClock.elapsedRealtime()
            timer.stop()
            pauseTime.visibility = View.GONE
            startTimer.visibility = View.VISIBLE
        }

        finishedButton.setOnClickListener{
            val li = LayoutInflater.from(applicationContext)
            val dialogView: View = li.inflate(R.layout.alert_dialog_workout, null)
            val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
            val workoutId = UUID.randomUUID().toString()
            val movements: ArrayList<Map<String, String>> = ArrayList()
            val builder = AlertDialog.Builder(this)
            val workoutName = dialogView.findViewById(R.id.nameWorkout) as EditText
            val workoutDescription = dialogView.findViewById(R.id.descriptionWorkout) as EditText


            val movement: HashMap<String, String> = HashMap()
            movement["home"] = home.text.toString()
            movement["away"] = away.text.toString()
            movements.add(movement)

            builder.setView(dialogView)

            // TODO:// https://www.journaldev.com/309/android-alert-dialog-using-kotlin
            builder.setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(applicationContext,
                    "Please wait", Toast.LENGTH_SHORT).show()
                loadingBar.visibility = View.VISIBLE
                timer.stop()


                if(workoutName.text.isEmpty()) {
                    workoutName.setText("Basketball Session")
                }

                if(workoutDescription.text.isEmpty()) {
                    workoutDescription.setText("General workout session")
                }

                //TODO: REF: https://developers.google.com/fit/android/using-sessions

                val session = Session.Builder()
                    .setName(workoutName.text.toString())
                    .setIdentifier(workoutId)
                    .setDescription(workoutDescription.text.toString())
                    .setActivity(FitnessActivities.BASKETBALL)
                    .setStartTime(startTime, TimeUnit.SECONDS)
                    .setEndTime(endTime, TimeUnit.SECONDS)
                    .build()

                val insertRequest = SessionInsertRequest.Builder()
                    .setSession(session)
                    .build()

                Fitness.getSessionsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                    .insertSession(insertRequest)
                    .addOnSuccessListener {
                        Log.i("TAG", "Session insert was successful!")

                        // ADD to Cloud Database now
                        val data = hashMapOf(
                            "name" to workoutName.text.toString(),
                            "description" to workoutDescription.text.toString(),
                            "startTime" to startTime.toString(),
                            "endTime" to endTime.toString(),
                            "duration" to endTime - startTime,
                            "id" to workoutId,
                            "type" to FitnessActivities.BASKETBALL,
                            "movements" to movements,
                        )
                        functions.getHttpsCallable("addWorkout")
                            .call(data)
                            .continueWith {
                                Log.d("WORKOUT",
                                    "Added Workout!")
                                loadingBar.visibility = View.INVISIBLE
                                finish()
                            }

                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "There was a problem inserting the session: ", e)
                    }


            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(applicationContext,
                    "Cancel", Toast.LENGTH_SHORT).show()
                timer.start()
            }


            builder.show()

        }

    }
}