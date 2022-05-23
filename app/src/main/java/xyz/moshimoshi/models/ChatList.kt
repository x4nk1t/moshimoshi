package xyz.moshimoshi.models

data class ChatList(
    var chatId: String? = null,
    var users: ArrayList<String>? = null,
    var lastMessage: String? = null,
    var lastMessageBy: String? = null
)