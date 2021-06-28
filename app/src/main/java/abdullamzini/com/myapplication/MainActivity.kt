package abdullamzini.com.myapplication

import abdullamzini.com.myapplication.adapters.ViewPagerAdapter
import abdullamzini.com.myapplication.fragments.FeedFragment
import abdullamzini.com.myapplication.fragments.MyProfileFragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabs)

        addTablelayoutImages()
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
        adapter.addFragmentTabs(MyProfileFragment(), "Profile")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        // set the icons to be used for each tab
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_feed_24)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_person_24)
//        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_drivers_24)
//        tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_package_24)
    }
}