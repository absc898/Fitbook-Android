package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.workouts.RecordBasketballActivity
import abdullamzini.com.myapplication.workouts.RecordRunningActivity
import abdullamzini.com.myapplication.workouts.RecordWeightLiftingActivity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.fitness.FitnessActivities

class SelectWorkoutActivity : AppCompatActivity() {

    private lateinit var workoutOptions: Spinner
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_workout)

        workoutOptions = findViewById(R.id.workoutSpinner)
        startButton = findViewById(R.id.startButton)


        ArrayAdapter.createFromResource(
            this,
            R.array.workout_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            workoutOptions.adapter = adapter
        }

        startButton.setOnClickListener{
            val selectedItem = workoutOptions.selectedItem.toString()
            if(selectedItem == "WeightLifting") {
                val intent = Intent(this, RecordWeightLiftingActivity::class.java)
                startActivity(intent)
            } else if(selectedItem == "Running") {
                val intent = Intent(this, RecordRunningActivity::class.java)
                intent.putExtra("type", FitnessActivities.RUNNING)
                startActivity(intent)
            } else if(selectedItem == "Walking") {
                val intent = Intent(this, RecordRunningActivity::class.java)
                intent.putExtra("type", FitnessActivities.WALKING)
                startActivity(intent)
            } else if(selectedItem == "Basketball") {
                val intent = Intent(this, RecordBasketballActivity::class.java)
                startActivity(intent)
            }
        }
    }
}