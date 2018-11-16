package chat.firebase.anwera97.firebasechat.utils

import android.text.Editable

class StringUtils {

    companion object {
        fun checkEmptyString(it: Editable): Boolean {
            return !it.toString().contentEquals("")
        }
    }
}