package chat.firebase.anwera97.firebasechat.utils

import android.content.Context
import chat.firebase.anwera97.firebasechat.R

class SharedPreferencesUtils {

    companion object {
        fun getType(context: Context, path: String, key: String): String {
            val pref = context.getSharedPreferences(path, Context.MODE_PRIVATE)!!
            return pref.getString(key, "None")!!
        }
    }
}