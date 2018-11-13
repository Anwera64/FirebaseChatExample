package chat.firebase.anwera97.firebasechat.Presentation.Presenters

import android.util.Log
import chat.firebase.anwera97.firebasechat.Data.Chat
import chat.firebase.anwera97.firebasechat.Data.Contact
import chat.firebase.anwera97.firebasechat.Data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class ChatsPresenter(val view: ChatsDelegate) {

    private val TAG = "Chats presenter"

    //Firebase
    private val dbRef = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun getChats() {
        dbRef.child("users/${currentUser!!.uid}/chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { chatKeySnap ->
                    val chatKey = chatKeySnap.value as String
                    getChat(chatKey)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

        })
    }

    private fun getChat(key: String) {
        dbRef.child("chats/$key").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val messages = ArrayList<Message>()
                p0.child("messages").children.forEach { messageSnap ->
                    val detail = messageSnap.child("detail").value as String
                    val from = messageSnap.child("from").value as String
                    val time = messageSnap.child("time").value as Long
                    val message = Message(messageSnap.key!!, detail, from, Date(time))
                    messages.add(message)
                }

                val type = view.obtainType()

                val chat = Chat(p0.key!!, messages)
                when (type) {
                    "alumnee" -> {
                        getContact(p0.child("teacher").value as String, chat)
                    }

                    "professor" -> {
                        getContact(p0.child("student").value as String, chat)
                    }
                }
            }

        })
    }

    private fun getContact(key: String, unfinishedChat: Chat) {
        dbRef.child("users/$key").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val contact = Contact(p0.key!!, p0.child("name").value as String)
                unfinishedChat.contact = contact

                view.onChatChanged(unfinishedChat)
            }
        })
    }

    interface ChatsDelegate {

        fun onChatChanged(updatedChat: Chat)

        fun obtainType(): String
    }
}

