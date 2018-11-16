package chat.firebase.anwera97.firebasechat.Presentation.Views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import chat.firebase.anwera97.firebasechat.Data.Message
import chat.firebase.anwera97.firebasechat.Presentation.Adapters.MessageAdapter
import chat.firebase.anwera97.firebasechat.Presentation.Presenters.ChatPresenter
import chat.firebase.anwera97.firebasechat.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity: AppCompatActivity(), ChatPresenter.ChatDelegate {

    private lateinit var mPresenter: ChatPresenter
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = getIntent()
        val chatId = intent.getStringExtra("chatID")

        mPresenter = ChatPresenter(this, chatId)

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(ArrayList(), this, mPresenter.getOwnId())
        chat_recycler_view.adapter = adapter
        scrollToLast()

        mPresenter.getMessages()
    }

    private fun scrollToLast() {
        chat_recycler_view.scrollToPosition(adapter.itemCount)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMessagesReady(messages: ArrayList<Message>) {
        adapter.messages = messages
        adapter.notifyDataSetChanged()
        scrollToLast()
    }

    override fun onMessageSent(message: Message) {

    }
}