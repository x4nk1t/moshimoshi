package xyz.moshimoshi.models

data class ChatList(
    var chatId: String? = null,
    var chatName: String? = null,
    var users: ArrayList<String>? = null,
    var chatLastMessage: String? = null,
    var chatLastMessageBy: String? = null,
    var chatLastMessageTimestamp: Long? = null
)