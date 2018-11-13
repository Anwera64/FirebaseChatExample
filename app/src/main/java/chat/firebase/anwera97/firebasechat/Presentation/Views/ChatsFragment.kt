package chat.firebase.anwera97.firebasechat.Presentation.Views

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.Data.Chat
import chat.firebase.anwera97.firebasechat.Presentation.Adapters.ChatAdapter
import chat.firebase.anwera97.firebasechat.Presentation.Presenters.ChatsPresenter
import chat.firebase.anwera97.firebasechat.R
import kotlinx.android.synthetic.main.fragment_chats.*
import java.util.*

class ChatsFragment : Fragment(), ChatsPresenter.ChatsDelegate {

    companion object {
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    private val mPresenter = ChatsPresenter(this)
    private var chats = Hashtable<String, Chat>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewChats.layoutManager = LinearLayoutManager(context)
        recyclerViewChats.adapter = ChatAdapter(getDataAsPseudoList(chats), context)

        mPresenter.getChats()
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
        recyclerViewChats.adapter = ChatAdapter(getDataAsPseudoList(chats), context)
    }

    override fun obtainType(): String {
        val pref = context!!.getSharedPreferences(getString(R.string.shared_preferences) ,Context.MODE_PRIVATE)!!
        return pref.getString(getString(R.string.saved_type), "None")!!
    }
}