package com.example.firebasechat_typek

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class FriendlyMessage(
    var text: String? = "",
    var name: String? = "",
    var timeStamp : String? = "",
    var fromUserId : String? = ""
)

