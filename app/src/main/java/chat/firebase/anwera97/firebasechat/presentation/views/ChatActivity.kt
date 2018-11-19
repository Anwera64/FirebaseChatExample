package chat.firebase.anwera97.firebasechat.presentation.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import chat.firebase.anwera97.firebasechat.data.Message
import chat.firebase.anwera97.firebasechat.presentation.adapters.MessageAdapter
import chat.firebase.anwera97.firebasechat.presentation.presenters.ChatPresenter
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity(), ChatPresenter.ChatDelegate {

    private lateinit var mPresenter: ChatPresenter
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val chatId = intent.getStringExtra("chatID")
        mPresenter = ChatPresenter(this, chatId)

        if (intent.hasExtra("contactID")) {
            val contactID = intent.getStringExtra("contactID")
            mPresenter.createNewChat(contactID)
        }

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(ArrayList(), this, mPresenter.getOwnId())
        chat_recycler_view.adapter = adapter
        scrollToLast()

        send_message_btn.setOnClickListener { sendMessage() }

        mPresenter.getMessages()
    }

    private fun scrollToLast() {
        chat_recycler_view.scrollToPosition(adapter.itemCount - 1)
    }

    private fun sendMessage() {
        val detail = message_detail.text.toString()
        if (detail.isEmpty()) return

        mPresenter.sendMessage(detail)
        message_detail.text.clear()
    }

    override fun obtainType(): String {
        return SharedPreferencesUtils.getType(
            this,
            getString(R.string.shared_preferences),
            getString(R.string.saved_type)
        )
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
}