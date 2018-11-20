package chat.firebase.anwera97.firebasechat.presentation.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.data.Message
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.utils.DateUtils
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_chat_from.view.*

class MessageAdapter(var messages: ArrayList<Message>, private val context: Context, private val ownId: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val storage = FirebaseStorage.getInstance().reference


    override fun getItemViewType(position: Int): Int {
        return when (messages[position].origin) {
            ownId -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageAdapter.ViewHolder {
        return when (p1) {
            0 -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_to, p0, false))
            else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_from, p0, false))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(p0: MessageAdapter.ViewHolder, p1: Int) {
        val message = messages[p1]
        p0.detail.text = message.detail

        p0.timestamp.text = DateUtils.format(DateUtils.hourAndMinute, message.date)

        message.file?.let {
            when (it.type.split("/").first()) {
                "image" -> {
                    Glide.with(context)
                        .load(storage.child(it.path))
                        .into(p0.image)
                    p0.detail.visibility = View.GONE
                    p0.image.visibility = View.VISIBLE
                }
                else -> {
                    p0.fileIcon.visibility = View.VISIBLE
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var detail = view.detail!!
        var timestamp = view.timestamp!!
        var fileIcon = view.fileIcon!!
        var image = view.imageContent!!
    }

}