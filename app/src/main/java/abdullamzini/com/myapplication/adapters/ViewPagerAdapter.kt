package abdullamzini.com.myapplication.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentTabLists = ArrayList<Fragment>()
    private val fragmentTabsTitles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragmentTabLists[position]
    }

    override fun getCount(): Int {
        return  fragmentTabLists.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTabsTitles[position]
    }

    fun addFragmentTabs(fragment: Fragment, title: String) {
        fragmentTabLists.add(fragment)
        fragmentTabsTitles.add(title)
    }
}