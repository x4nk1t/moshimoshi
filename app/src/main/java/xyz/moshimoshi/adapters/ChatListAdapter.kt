package xyz.moshimoshi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.moshimoshi.R
import xyz.moshimoshi.models.ChatList

class ChatListAdapter(private val dataSet: ArrayList<ChatList>):
    RecyclerView.Adapter<ChatListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatlist, parent, false)

        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val data = dataSet[position]
        holder.chatName.text = data.chatId
        holder.lastMessage.text = data.lastMessage
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view){
    val chatName: TextView = view.findViewById(R.id.chat_name)
    val lastMessage: TextView = view.findViewById(R.id.lastMessage)
}