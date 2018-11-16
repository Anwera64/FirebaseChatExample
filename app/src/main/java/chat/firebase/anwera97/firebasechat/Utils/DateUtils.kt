package chat.firebase.anwera97.firebasechat.Utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        private val locale = Locale("es", "PE")

        val hourAndMinute = SimpleDateFormat("k:m", locale)

        fun format(df: SimpleDateFormat, dateString: Date): String {
            return df.format(dateString)
        }
    }
}