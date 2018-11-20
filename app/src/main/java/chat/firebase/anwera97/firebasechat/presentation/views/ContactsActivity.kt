package chat.firebase.anwera97.firebasechat.presentation.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.data.Contact
import chat.firebase.anwera97.firebasechat.presentation.adapters.ContactAdapter
import chat.firebase.anwera97.firebasechat.presentation.presenters.ContactsPresenter
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*

class ContactsActivity : AppCompatActivity(), ContactsPresenter.ContactsDelegate,
    ContactAdapter.ContactAdapterDelegate {


    private val mPresenter = ContactsPresenter(this)
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(ArrayList(), this, this)
        recyclerViewContacts.adapter = adapter

        mPresenter.getContacts()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onContactReady(contact: Contact) {
        if (!adapter.contacts.contains(contact)) {
            adapter.contacts.add(contact)
        } else {
            val index = adapter.contacts.indexOf(contact)
            adapter.contacts[index] = contact
        }

        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        mPresenter.deleteListeners()
        super.onDestroy()
    }

    override fun onContactPressed(id: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("contactID", id)
        val chatID = "$id-${mPresenter.user.uid}"
        intent.putExtra("chatID", chatID)
        startActivity(intent)
    }
}