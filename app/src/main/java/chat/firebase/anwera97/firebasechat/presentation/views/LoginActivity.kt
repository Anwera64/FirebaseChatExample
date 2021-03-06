package chat.firebase.anwera97.firebasechat.presentation.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import chat.firebase.anwera97.firebasechat.presentation.presenters.LoginPresenter
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.utils.StringUtils
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginPresenter.LoginDelegate {

    private val mPresenter = LoginPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (mPresenter.doLogin()) {
            correctLogin()
        }

        email_sign_in_button.setOnClickListener { tryLogin() }
    }

    private fun tryLogin() {
        if (!checkForCompletion()) return
        mPresenter.doLogin(email.text.toString(), password.text.toString())
    }

    private fun checkForCompletion(): Boolean {
        var result = false

        email.text.let {
            result = StringUtils.checkEmptyString(it)
        }

        password.text.let {
            result = StringUtils.checkEmptyString(it)
        }

        return result
    }

    private fun correctLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun incorrectLogin(message: String) {
        Log.e("Login error:", message)
    }

    override fun saveType(type: String) {
        val pref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE) ?: return

        with(pref.edit()) {
            putString(getString(R.string.saved_type), type)
            commit()
        }

        correctLogin()
    }

}
