package chat.firebase.anwera97.firebasechat.Presentation.Presenters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
                    getCompleteChat(chatKey)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

        })
    }

    private fun getCompleteChat(key: String) {
        dbRef.child("chats/$key").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {

            }

        })
    }
}

interface ChatsDelegate {

    fun onChatsChanged()
}