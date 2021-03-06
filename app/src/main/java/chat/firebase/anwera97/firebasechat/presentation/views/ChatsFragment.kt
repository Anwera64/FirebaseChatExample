package chat.firebase.anwera97.firebasechat.presentation.views

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.data.Chat
import chat.firebase.anwera97.firebasechat.presentation.adapters.ChatAdapter
import chat.firebase.anwera97.firebasechat.presentation.presenters.ChatsPresenter
import kotlinx.android.synthetic.main.fragment_chats.*
import java.util.*

class ChatsFragment : Fragment(), ChatsPresenter.ChatsDelegate, ChatAdapter.ChatAdapterDelegate {

    companion object {
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    private val mPresenter = ChatsPresenter(this)
    private var chats = Hashtable<String, Chat>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewChats.layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(getDataAsPseudoList(chats), context, this)
        recyclerViewChats.adapter = adapter

        newChat.setOnClickListener { onNewContactPressed() }

        progressBar.visibility = View.VISIBLE
        mPresenter.getChats()
    }

    private fun onNewContactPressed() {
        val intent = Intent(context, ContactsActivity::class.java)
        startActivity(intent)
    }

    override fun onContactPressed(chatId: String, name: String) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("chatID", chatId)
        intent.putExtra("contactName", name)
        startActivity(intent)
    }

    private fun getDataAsPseudoList(data: Hashtable<String, Chat>): Hashtable<Int, Chat> {
        val result = Hashtable<Int, Chat>()
        var index = 0

        data.forEach { chat ->
            result[index] = chat.value
            index++
        }

        return result
    }

    override fun onChatChanged(updatedChat: Chat) {
        chats[updatedChat.id] = updatedChat
        adapter.chats = getDataAsPseudoList(chats)
        adapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE
    }
}