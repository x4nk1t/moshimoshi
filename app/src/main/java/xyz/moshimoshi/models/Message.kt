package xyz.moshimoshi.models

class Message(
    var id: String? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var message: String? = null,
    var timestamp: Long? = null,
    var readTimestamp: Long? = null,
){
    fun toHash(): HashMap<String, Any> {
        val messageHashMap = HashMap<String, Any>()

        messageHashMap["chats_id"] = chatId!!
        messageHashMap["receiverId"] = receiverId!!
        messageHashMap["senderId"] = senderId!!
        messageHashMap["timestamp"] = System.currentTimeMillis()
        messageHashMap["readTimestamp"] = 0
        messageHashMap["message"] = message!!

        return messageHashMap
    }
}