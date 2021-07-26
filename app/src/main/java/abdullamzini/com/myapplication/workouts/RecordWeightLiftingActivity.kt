package abdullamzini.com.myapplication.workouts

import abdullamzini.com.myapplication.R
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


class RecordWeightLiftingActivity : AppCompatActivity() {

    private lateinit var listTexts: LinearLayout
    private lateinit var add: Button
    private lateinit var tableList: TableLayout

    private lateinit var timer: Chronometer
    private lateinit var startTimer: Button
    private lateinit var pauseTime: Button
    private lateinit var finishedButton: Button

    private lateinit var functions: FirebaseFunctions
    private lateinit var fitnessOptions: FitnessOptions

    var GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 347


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_weight_lifting)
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
        add = findViewById(R.id.addItemButton)
        timer = findViewById(R.id.textViewStopWatch)
        tableList = findViewById(R.id.tableList)
        finishedButton = findViewById(R.id.doneButton)

        fitnessOptions = FitnessOptions.builder()
            .accessActivitySessions(FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WEIGHT,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WORKOUT_EXERCISE,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_MOVE_MINUTES,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_DISTANCE_DELTA,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_LOCATION_SAMPLE,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_POWER_SAMPLE,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_STEP_COUNT_CADENCE,FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_LOCATION_SAMPLE, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions)
        } else {
            accessGoogleFit()
        }

        startTimer.setOnClickListener{
            startTime = System.currentTimeMillis()
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

        add.setOnClickListener{
            val row: TableRow = LayoutInflater.from(this)
                .inflate(R.layout.table_list_temp, null) as TableRow
            (row.findViewById(R.id.attrib_name) as TextView).setText("bi")
            (row.findViewById(R.id.attrib_value) as TextView).setText("100")
            tableList.addView(row)
        }

        finishedButton.setOnClickListener{
            val endTime = startTime + timer.base
            val workoutId = UUID.randomUUID().toString()
            timer.stop()

            val session = Session.Builder()
                .setName("WeightLifting Testing Abs")
                .setIdentifier(workoutId)
                .setDescription("Morning Session")
                .setActivity(FitnessActivities.WEIGHTLIFTING)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
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
                        "name" to "WeightLifting",
                        "startTime" to startTime.toString(),
                        "endTime" to endTime.toString(),
                        "id" to workoutId,
                        "type" to FitnessActivities.WEIGHTLIFTING
                    )
                    functions.getHttpsCallable("addWorkout")
                        .call(data)
                        .continueWith {
                                Log.d("WORKOUT",
                                    "Added Workout!")
                                finish()
                        }

                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "There was a problem inserting the session: ", e)
                }
        }


//        listTexts = findViewById(R.id.entry_list)
//        add = findViewById(R.id.addItemButton)
//        add.setOnClickListener{
//            val tv = TextView(this)
////            tv.width = ActionBar.LayoutParams.MATCH_PARENT
//            tv.text = "Test"
//            listTexts.addView(tv)
//        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> accessGoogleFit()
                else -> {
                    // Result wasn't from Google Fit
                }
            }
            else -> {
                // Permission not granted
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun accessGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        Fitness.getHistoryClient(this, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // Use response data here
                Log.i("TAG", "OnSuccess()")
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "OnFailure()", e)
            }
    }

}