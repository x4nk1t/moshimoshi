package xyz.moshimoshi.models

class Message(
    var id: String? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var message: String? = null,
    var timestamp: String? = null,
    var readTimestamp: String? = null,
)