package abdullamzini.com.myapplication.workouts

import abdullamzini.com.myapplication.R
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class RecordWeightLiftingActivity : AppCompatActivity() {

    private lateinit var listTexts: LinearLayout
    private lateinit var add: Button
    private lateinit var tableList: TableLayout

    private lateinit var timer: Chronometer
    private lateinit var startTimer: Button
    private lateinit var pauseTime: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_weight_lifting)
        var stopTime: Long = 0

        startTimer = findViewById(R.id.startTimeButton)
        pauseTime = findViewById(R.id.pauseButton)
        add = findViewById(R.id.addItemButton)
        timer = findViewById(R.id.textViewStopWatch)
        tableList = findViewById(R.id.tableList)

        startTimer.setOnClickListener{
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

//        listTexts = findViewById(R.id.entry_list)
//        add = findViewById(R.id.addItemButton)
//        add.setOnClickListener{
//            val tv = TextView(this)
////            tv.width = ActionBar.LayoutParams.MATCH_PARENT
//            tv.text = "Test"
//            listTexts.addView(tv)
//        }

    }

}