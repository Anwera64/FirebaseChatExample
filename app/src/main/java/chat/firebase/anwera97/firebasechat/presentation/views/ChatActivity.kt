package chat.firebase.anwera97.firebasechat.presentation.views

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.webkit.MimeTypeMap
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.data.Message
import chat.firebase.anwera97.firebasechat.presentation.adapters.MessageAdapter
import chat.firebase.anwera97.firebasechat.presentation.presenters.ChatPresenter
import chat.firebase.anwera97.firebasechat.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity(), ChatPresenter.ChatDelegate {

    private lateinit var mPresenter: ChatPresenter
    private lateinit var adapter: MessageAdapter

    private val READ_REQUEST_CODE: Int = 42

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

        if (intent.hasExtra("contactName")) {
            val contactName = intent.getStringExtra("contactName")
            supportActionBar?.title = contactName
        }

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(ArrayList(), this, mPresenter.getOwnId())
        chat_recycler_view.adapter = adapter
        scrollToLast()

        send_message_btn.setOnClickListener { sendMessage() }
        file_button.setOnClickListener { addFile() }

        mPresenter.getMessages()
    }

    private fun scrollToLast() {
        chat_recycler_view.scrollToPosition(adapter.itemCount - 1)
    }

    private fun addFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as a
            // file (as opposed to a list of contacts or timezones)
            addCategory(Intent.CATEGORY_OPENABLE)

            // To search for all documents available via installed storage providers,
            // it would be "*/*".
            type = "*/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            data?.data?.also { uri ->
                defineFileType(uri)?.let {
                    mPresenter.uploadFile(uri, it)
                }
            }
        }
    }

    private fun defineFileType(uri: Uri): String? {
        val mimeType: String?
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = this.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
        return mimeType
    }

    private fun sendMessage() {
        val detail = message_detail.text.toString()
        if (detail.isEmpty()) return

        mPresenter.sendSimpleMessage(detail)
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