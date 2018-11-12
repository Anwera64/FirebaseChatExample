package chat.firebase.anwera97.firebasechat.Presentation.Presenters

import com.google.firebase.auth.FirebaseAuth

class LoginPresenter(val view: LoginDelegate) {

    private val mAuth = FirebaseAuth.getInstance()

    fun doLogin(): Boolean {
        mAuth.currentUser.let {
            return true
        }
    }

    fun doLogin(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { view.correctLogin() }
            .addOnFailureListener { exception ->  view.incorrectLogin(exception.localizedMessage) }
    }

    interface LoginDelegate{
        fun correctLogin()

        fun incorrectLogin(message : String)
    }
}