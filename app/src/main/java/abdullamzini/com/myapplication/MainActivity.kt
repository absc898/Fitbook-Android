package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.ViewPagerAdapter
import abdullamzini.com.myapplication.fragments.FeedFragment
import abdullamzini.com.myapplication.fragments.FitnessFragment
import abdullamzini.com.myapplication.fragments.FriendsFragment
import abdullamzini.com.myapplication.fragments.MyProfileFragment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private lateinit var addButton: BoomMenuButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        functions = Firebase.functions

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabs)

        addTablelayoutImages()

        addButton = findViewById(R.id.bmb)

        for (i in 0 until addButton.getPiecePlaceEnum().pieceNumber()) {
            var image = R.drawable.ic_baseline_camera_alt_24
            var text = "Add Post"
            if(i == 1) {
                image = R.drawable.ic_baseline_sports_24
                text = "Record Workout"
            } else if (i == 2) {
                image = R.drawable.ic_baseline_draw_24
                text = "Add Activity"
            }
            val builder = TextInsideCircleButton.Builder().normalText(text).normalImageRes(image)
                .listener { index ->
                    // When the boom-button corresponding this builder is clicked.
                    if(index == 0) {
                        val intent = Intent(this, AddPostActivity::class.java)
                        startActivity(intent)
                    } else if(index == 1) {
                        val intent = Intent(this, SelectWorkoutActivity::class.java)
                        startActivity(intent)
                    }
                    Toast.makeText(this, "Clicked $index", Toast.LENGTH_SHORT).show()

                }
            addButton.addBuilder(builder)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.addPost -> {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.sync -> {
            syncGoogleFit()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun addTablelayoutImages() { // all the tabs we need for the features listed
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragmentTabs(FeedFragment(), "Feed")
        adapter.addFragmentTabs(FitnessFragment(), "Workout")
        adapter.addFragmentTabs(FriendsFragment(), "Friends")
        adapter.addFragmentTabs(MyProfileFragment(), "Profile")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        // set the icons to be used for each tab
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_feed_24)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_sports_24)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_friends_24)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_baseline_person_24)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun syncGoogleFit() {
        val workoutIds = arrayListOf<String>()
        val sessionIds = arrayListOf<String>()
        val docRef = db.collection("users").document(auth.currentUser!!.uid.toString()).collection("workouts")
        docRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    workoutIds.add(document.id.toString())
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                347)
        }

        val fitnessOptions = FitnessOptions.builder()
            .accessActivitySessions(FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .build()

        // Use a start time of 1 week ago and an end time of now.
        val endTime1 = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime1 = endTime1.minusWeeks(52)

        // Build a session read request
        val readRequest = SessionReadRequest.Builder()
            .setTimeInterval(startTime1.toEpochSecond(), endTime1.toEpochSecond(), TimeUnit.SECONDS)
            .readSessionsFromAllApps()
            .enableServerQueries()
            .build()

        Fitness.getSessionsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readSession(readRequest)
            .addOnSuccessListener { response ->

                // Get a list of the sessions that match the criteria to check the result.
                val sessions = response.sessions
                Log.i("TAG", "Number of returned sessions is: ${sessions.size}")
                for (session in sessions) {
                    sessionIds.add(session.identifier)
                    val size = workoutIds.size
                    // Below adding session
                    if(!workoutIds.contains(session.identifier)){
                        val movements: ArrayList<Map<String, String>> = ArrayList()
                        val startTime = session.getStartTime(TimeUnit.SECONDS)
                        val endTime =session.getEndTime(TimeUnit.SECONDS)

                        // ADD to Cloud Database now
                        val data = hashMapOf(
                            "name" to session.name,
                            "description" to session.description,
                            "startTime" to startTime.toString(),
                            "endTime" to endTime.toString(),
                            "duration" to endTime - startTime,
                            "id" to session.identifier,
                            "type" to session.activity,
                            "movements" to  movements,
                        )
                        functions.getHttpsCallable("addWorkout")
                            .call(data)
                            .continueWith {
                                Log.d("WORKOUT",
                                    "Added Workout!")
                            }
                    }

                }

                // Remove delete session
                for(id in workoutIds) {
                    if(!sessionIds.contains(id)) {
                        val data = hashMapOf(
                            "id" to id,
                        )
                        functions.getHttpsCallable("deleteWorkout")
                            .call(data)
                            .continueWith {
                                Log.d("WORKOUT", "Deleted Workout!")
                                finish()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG","Failed to read session", e)
            }
    }
}