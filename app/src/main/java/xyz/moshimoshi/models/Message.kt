package xyz.moshimoshi.models

class Message(
    var id: String? = null,
    var chatId: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var message: String? = null,
    var timestamp: String? = null,
    var readTimestamp: String? = null,
){
    fun toHash(): HashMap<String, String> {
        val messageHashMap = HashMap<String, String>()

        messageHashMap["chats_id"] = chatId!!
        messageHashMap["receiverId"] = receiverId!!
        messageHashMap["senderId"] = senderId!!
        messageHashMap["timestamp"] = System.currentTimeMillis().toString()
        messageHashMap["readTimestamp"] = "0"
        messageHashMap["message"] = message!!

        return messageHashMap
    }
}