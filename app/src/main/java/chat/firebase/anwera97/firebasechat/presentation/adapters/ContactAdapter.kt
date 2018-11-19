package chat.firebase.anwera97.firebasechat.presentation.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.data.Contact
import chat.firebase.anwera97.firebasechat.R
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(var contacts: ArrayList<Contact>, private val context: Context, private val view: ContactAdapterDelegate): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, p0, false))
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val contact = contacts[p1]
        p0.name.text = contact.name
        p0.itemView.setOnClickListener { view.onContactPressed(contact.id) }
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name = view.tvName!!
        val image = view.ivContact!!
    }

    interface ContactAdapterDelegate {
        fun onContactPressed(id: String)
    }
}