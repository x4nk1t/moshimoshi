package xyz.moshimoshi.models

data class ChatList(
    var chatId: String? = null,
    var chatName: String? = null,
    var users: ArrayList<String>? = null,
    val chatLastMessage: String? = null,
    val chatLastMessageBy: String? = null,
    val chatLastMessageTimestamp: Long? = null
)