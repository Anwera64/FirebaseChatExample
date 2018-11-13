package chat.firebase.anwera97.firebasechat.Presentation.Views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.Utils.ViewPager.SimplePagerAdapter
import chat.firebase.anwera97.firebasechat.Utils.ViewPager.TabLayoutHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val pagerAdapter = SimplePagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        viewPager.adapter = pagerAdapter

        pagerAdapter.addFragment(ChatsFragment.newInstance(), "Chats", R.drawable.ic_chats)
        pagerAdapter.addFragment(Fragment(), "Profile", R.drawable.ic_profile)
        updatePagerAdapter()
        updatetabs()
    }

    fun updatePagerAdapter() {
        pagerAdapter.notifyDataSetChanged()
    }

    fun updatetabs() {
        tabs.removeAllTabs()
        if (pagerAdapter.count > 1) {
            tabs.visibility = android.view.View.VISIBLE
            TabLayoutHelper.setupWithViewPager(tabs, viewPager)
        } else {
            tabs.visibility = android.view.View.GONE
        }
    }
}