package chat.firebase.anwera97.firebasechat.data

import java.util.*

class Message(val id: String, val detail: String, val origin: String, val date: Date, val file: StorageFile?) {

    constructor(id: String, detail: String, origin: String, date: Date) : this(id, detail, origin, date, null)
}