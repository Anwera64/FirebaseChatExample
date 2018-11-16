package chat.firebase.anwera97.firebasechat.Presentation.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.Data.Chat
import chat.firebase.anwera97.firebasechat.R
import kotlinx.android.synthetic.main.contact_item.view.*
import java.util.*

class ContactAdapter(
    var chats: Hashtable<Int, Chat>,
    private val context: Context?,
    private val view: ContactAdapterDelegate
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_item, p0, false))
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val chat = chats[p1]!!
        p0.tvName.text = chat.contact.name
        p0.tvLastText.text = chat.messages.last().detail
        p0.itemView.setOnClickListener {
            view.onContactPressed(chat.id)
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tvContactName!!
        val tvLastText = view.tvLastMessage!!
    }

    interface ContactAdapterDelegate {
        fun onContactPressed(chatId: String)
    }
}