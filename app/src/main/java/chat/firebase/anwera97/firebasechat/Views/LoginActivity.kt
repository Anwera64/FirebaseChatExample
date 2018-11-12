package chat.firebase.anwera97.firebasechat.Views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import chat.firebase.anwera97.firebasechat.Presenters.LoginPresenter
import chat.firebase.anwera97.firebasechat.R
import chat.firebase.anwera97.firebasechat.Utils.StringUtils
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginPresenter.LoginDelegate {

    private val mPresenter = LoginPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

    override fun correctLogin() {
        val intent = Intent(this, ChatsActivity::class.java)
        startActivity(intent)
    }

    override fun incorrectLogin(message: String) {
        Log.e("Login error:", message)
    }


}
