package chat.firebase.anwera97.firebasechat.presentation.presenters

import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.util.Log
import chat.firebase.anwera97.firebasechat.data.Message
import chat.firebase.anwera97.firebasechat.data.StorageFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatPresenter(val view: ChatDelegate, private val chatId: String) {

    private val TAG = "Chat presenter"

    //Firebase
    private val db = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val storage = FirebaseStorage.getInstance().reference
    private val path = "chats/$chatId/messages"


    fun getMessages() {
        db.child(path).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val messages = ArrayList<Message>()
                p0.children.forEach { messageSnap ->
                    val detail = messageSnap.child("detail").value as String
                    val date = messageSnap.child("time").value as Long
                    val origin = messageSnap.child("from").value as String
                    var file: StorageFile? = null

                    if (messageSnap.hasChild("file")) {
                        file = createFile(messageSnap.child("file"))
                    }

                    val message = Message(messageSnap.key!!, detail, origin, Date(date), file)
                    messages.add(message)
                }

                messages.sortWith(compareBy { it.date })
                view.onMessagesReady(messages)
            }
        })
    }

    private fun createFile(fileSnapshot: DataSnapshot): StorageFile {
        val path = fileSnapshot.child("path").value as String
        val type = fileSnapshot.child("type").value as String

        return StorageFile(path, type)
    }

    fun createNewChat(contactId: String) {
        db.child("chats/$chatId").updateChildren(chatJSON(contactId))
        db.child("users/$contactId/chats/$chatId").setValue(user.uid)
        db.child("users/${user.uid}/chats/$chatId").setValue(contactId)
    }

    private fun chatJSON(contactId: String): HashMap<String, Any> {
        val hash = HashMap<String, Any>()
        when (view.obtainType()) {
            "alumnee" -> {
                hash["student"] = user.uid
                hash["teacher"] = contactId
            }

            "professor" -> {
                hash["teacher"] = user.uid
                hash["student"] = contactId
            }
        }

        return hash
    }

    fun downloadFile(firebaseStoragePath: String, internalStorageFile: String, type: String) {
        val ref = storage.child(firebaseStoragePath)
        val intoFile = File(internalStorageFile+ref.name)
        ref.getFile(intoFile)
            .addOnSuccessListener {
                view.onFileDownloaded(it.storage.name, type)
            }.addOnFailureListener {
                Log.e(TAG, it.localizedMessage, it.cause)
            }
    }

    fun uploadFile(file: Uri, type: String, name: String) {
        val filePath = "$path/${UUID.randomUUID()}"
        val riversRef = storage.child(filePath)
        val uploadTask = riversRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.e(TAG, it.localizedMessage, it.cause)
        }.addOnSuccessListener {
            sendFileMessage(name, filePath, type)
        }
    }

    private fun sendFileMessage(detail: String, path: String, type: String) {
        sendMessage(detail, path, type)
    }

    private fun sendMessage(detail: String, filePath: String?, type: String?) {
        val uid = UUID.randomUUID().toString()
        val fileData = Pair(filePath, type)
        db.child("$path/$uid").setValue(messageJSON(detail, fileData))
    }

    fun sendSimpleMessage(detail: String) {
        sendMessage(detail, null, null)
    }

    private fun messageJSON(detail: String, fileData: Pair<String?, String?>): HashMap<String, Any> {
        val hash = HashMap<String, Any>()
        hash["detail"] = detail
        hash["from"] = user.uid
        hash["time"] = Date().time

        fileData.first?.let {
            val fileHash = HashMap<String, Any>()
            fileHash["path"] = it
            fileHash["type"] = fileData.second!!
            hash["file"] = fileHash
        }

        return hash
    }

    fun getOwnId(): String {
        return user.uid
    }

    interface ChatDelegate {
        fun onMessagesReady(messages: ArrayList<Message>)

        fun obtainType(): String

        fun onFileDownloaded(name: String, type: String)
    }
}