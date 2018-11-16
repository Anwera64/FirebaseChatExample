package chat.firebase.anwera97.firebasechat.Presentation.Adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.Data.Message
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.Utils.DateUtils
import kotlinx.android.synthetic.main.chat_item_from.view.*

class MessageAdapter(var messages: ArrayList<Message>, private val context: Context, private val ownId: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return when (messages[position].origin) {
            ownId -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageAdapter.ViewHolder {
        return when (p1) {
            0 -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_to, p0, false))
            else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_from, p0, false))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(p0: MessageAdapter.ViewHolder, p1: Int) {
        val message = messages[p1]
        p0.detail.text = message.detail

        p0.timestamp.text = DateUtils.format(DateUtils.hourAndMinute, message.date)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var detail = view.detail!!
        var timestamp = view.timestamp!!
    }

}