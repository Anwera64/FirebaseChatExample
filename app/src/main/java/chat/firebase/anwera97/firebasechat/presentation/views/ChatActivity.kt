package chat.firebase.anwera97.firebasechat.presentation.views

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.data.Message
import chat.firebase.anwera97.firebasechat.presentation.adapters.MessageAdapter
import chat.firebase.anwera97.firebasechat.presentation.presenters.ChatPresenter
import chat.firebase.anwera97.firebasechat.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.io.File.separator


class ChatActivity : AppCompatActivity(), ChatPresenter.ChatDelegate, MessageAdapter.MessageAdapterDelegate {

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
        adapter = MessageAdapter(ArrayList(), this, this, mPresenter.getOwnId())
        chat_recycler_view.adapter = adapter

        send_message_btn.setOnClickListener { sendMessage() }
        file_button.setOnClickListener { addFile() }

        mPresenter.getMessages()
        if (adapter.itemCount > 0) scrollToLast()
    }

    private fun scrollToLast() {
        chat_recycler_view.scrollToPosition(adapter.itemCount - 1)
    }

    override fun openFile(path: String, type: String) {
        if (!checkForWritingPermissions()) return
        val internalPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + separator
        progressBar.visibility = View.VISIBLE
        blocking_view.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        mPresenter.downloadFile(path, internalPath, type)
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

    private fun checkForWritingPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            return false
        }

        return true
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
                    mPresenter.uploadFile(uri, it, getFileName(uri))
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)

        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }

        return "File"
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

    override fun onFileDownloaded(name: String, type: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + separator +
                    name
        )
        val path = FileProvider.getUriForFile(
            this,
            "chat.firebase.anwera97.firebasechat",
            file
        )

        progressBar.visibility = View.GONE
        blocking_view.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(path, type)
        startActivity(intent)
    }
}