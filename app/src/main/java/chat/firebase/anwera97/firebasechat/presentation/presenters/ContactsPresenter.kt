package chat.firebase.anwera97.firebasechat.presentation.presenters

import android.util.Log
import chat.firebase.anwera97.firebasechat.data.Contact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsPresenter(val view: ContactsDelegate) {

    private val TAG = "Contacts Presenter"

    //Firebase
    private val db = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser!!
    private val listeners = ArrayList<ValueEventListener>()

    fun getContacts() {
        listeners.add(db.child("users/${user.uid}/courses").orderByChild("teacher").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val keys = HashSet<String>()

                p0.children.forEach { courseSnapshot ->
                    val contactKey = courseSnapshot.child("teacher").value as String
                    if (!keys.contains(contactKey)) {
                        keys.add(contactKey)
                        getTeacher(contactKey)
                    }
                }
            }

        }))
    }

    private fun getTeacher(key: String) {
        listeners.add(db.child("users/$key").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("name").value as String
                val contact = Contact(p0.key!!, name)
                view.onContactReady(contact)
            }

        }))
    }

    fun deleteListeners() {
        listeners.forEach { listener ->
            db.removeEventListener(listener)
        }
    }


    interface ContactsDelegate {
        fun onContactReady(contact: Contact)
    }
}