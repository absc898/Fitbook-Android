package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.WorkoutEntity
import abdullamzini.com.myapplication.workouts.DetailedWeightActivity
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.fitness.FitnessActivities
import kotlinx.android.synthetic.main.adapter_workout.view.*
import java.util.*

class WorkoutAdapter (options: FirestoreRecyclerOptions<WorkoutEntity>) : FirestoreRecyclerAdapter<WorkoutEntity, WorkoutAdapter.WorkoutAdapterVH>(
    options
) {

    // TODO: Need to reference FIREBASE UI Cloudstore for below code

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutAdapter.WorkoutAdapterVH {
        return WorkoutAdapter.WorkoutAdapterVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_workout,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WorkoutAdapterVH, position: Int, model: WorkoutEntity) {
        //var movements = model.getMovements()
        var activityType = model.getType()

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = (model.getStartTime()?.toLong() ?: 0) * 1000L
        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
        val s = model.getDuration()
        val duration =((s/3600).toString() + ':' +((s/60)%60) + ":"  + (s%60))

        holder.startTime.text = date
        holder.workoutName.text = model.getWorkoutName()
        holder.duration.text = duration

        if(activityType == FitnessActivities.WEIGHTLIFTING) {
            holder.icon.setImageResource(R.drawable.ic_baseline_weight_24)
        } else if (activityType == FitnessActivities.RUNNING) {
            holder.icon.setImageResource(R.drawable.ic_baseline_run_24)
        } else if (activityType == FitnessActivities.WALKING) {
            holder.icon.setImageResource(R.drawable.ic_baseline_nordic_walking_24)
        } else if(activityType == FitnessActivities.BASKETBALL) {
            holder.icon.setImageResource(R.drawable.ic_baseline_sports_basketball_24)
        }

        holder.cardView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DetailedWeightActivity::class.java)
            intent.putExtra("name", model.getWorkoutName())
            intent.putExtra("id", model.getWorkoutID())
            intent.putExtra("description", model.getDescription())
            intent.putExtra("movements", model.getMovements())
            intent.putExtra("startTime", date)
            intent.putExtra("startTimeSeconds", model.getStartTime())
            intent.putExtra("endTime", model.getEndTime())
            intent.putExtra("type", activityType)
            holder.itemView.context.startActivity(intent)
        }

    }

    class WorkoutAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var workoutName = itemView.workoutName
        var duration = itemView.workoutDuration
        var startTime = itemView.workoutStartTime
        var icon = itemView.workoutIIcon
        var cardView = itemView.cardView
    }

}