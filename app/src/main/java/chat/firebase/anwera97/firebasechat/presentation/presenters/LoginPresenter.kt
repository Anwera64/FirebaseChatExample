package chat.firebase.anwera97.firebasechat.presentation.presenters

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginPresenter(val view: LoginDelegate) {

    private val TAG = "LoginPresenter"

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun doLogin(): Boolean {
        mAuth.currentUser?.let {
            return true
        }

        return false
    }

    fun doLogin(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                saveType(it.user.uid)
            }
            .addOnFailureListener { exception -> view.incorrectLogin(exception.localizedMessage) }
    }

    private fun saveType(id: String) {

        db.child("users/$id/type").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e(TAG, p0.message, p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                view.saveType(p0.value as String)
            }

        })

    }

    interface LoginDelegate {

        fun incorrectLogin(message: String)

        fun saveType(type: String)
    }
}