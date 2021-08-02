package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.ViewPagerAdapter
import abdullamzini.com.myapplication.fragments.FeedFragment
import abdullamzini.com.myapplication.fragments.FitnessFragment
import abdullamzini.com.myapplication.fragments.FriendsFragment
import abdullamzini.com.myapplication.fragments.MyProfileFragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private lateinit var addButton: BoomMenuButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.addPost -> {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
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
}