package abdullamzini.com.myapplication.adapters

import abdullamzini.com.myapplication.R
import abdullamzini.com.myapplication.entities.WorkoutEntity
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        var movements = model.getMovements()

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = (model.getStartTime()?.toLong() ?: 0) * 1000L
        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
        val s = model.getDuration()
        val duration =((s/3600).toString() + ':' +((s/60)%60) + ":"  + (s%60))

        holder.startTime.text = date
        holder.workoutName.text = model.getWorkoutName()
        holder.duration.text = duration

        if(model.getType() == FitnessActivities.WEIGHTLIFTING) {
            holder.icon.setImageResource(R.drawable.ic_baseline_weight_24)
        } else {
            holder.icon.setImageResource(R.drawable.ic_baseline_run_24)
        }

        holder.cardView.setOnClickListener{
            Toast.makeText(holder.cardView.context,
                "Item select: ${model.getWorkoutName()}", Toast.LENGTH_SHORT).show()
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