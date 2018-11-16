package chat.firebase.anwera97.firebasechat.Presentation.Presenters

import android.content.Context
import android.util.Log
import chat.firebase.anwera97.firebasechat.Data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ChatPresenter(val view: ChatDelegate, private val chatId: String) {

    private val TAG = "Chat presenter"

    //Firebase
    private val db = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser!!


    fun getMessages() {
        db.child("chats/$chatId/messages").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val messages = ArrayList<Message>()
                p0.children.forEach { messageSnap ->
                    val detail = messageSnap.child("detail").value as String
                    val date = messageSnap.child("time").value as Long
                    val origin = messageSnap.child("from").value as String
                    val message = Message(messageSnap.key!!, detail, origin, Date(date))
                    messages.add(message)
                }

                view.onMessagesReady(messages)
            }
        })
    }

    fun sendMessage() {

    }

    fun getOwnId(): String {
        return user.uid
    }

    interface ChatDelegate {
        fun onMessagesReady(messages: ArrayList<Message>)

        fun onMessageSent(message: Message)
    }
}