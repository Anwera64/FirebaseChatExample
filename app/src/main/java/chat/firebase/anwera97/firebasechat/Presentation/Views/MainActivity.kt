package chat.firebase.anwera97.firebasechat.Presentation.Views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.Utils.ViewPager.SimplePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = SimplePagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter

        pagerAdapter.addFragment(ChatsFragment.newInstance(), "Chats", R.drawable.ic_chats)
        pagerAdapter.addFragment(Fragment(), "Profile", R.drawable.ic_profile)
    }
}