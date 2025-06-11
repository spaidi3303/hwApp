package com.sammy.hwapp.Group

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    var id: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var text: String = "",
    @ServerTimestamp
    var timestamp: Date? = null
) {
    constructor() : this("", "", "", "", null)
}