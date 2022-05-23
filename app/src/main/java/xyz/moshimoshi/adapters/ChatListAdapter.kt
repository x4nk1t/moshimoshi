package xyz.moshimoshi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import xyz.moshimoshi.R
import xyz.moshimoshi.models.ChatList

class ChatListAdapter(private val context: Context, private val dataSet: ArrayList<ChatList>):
    RecyclerView.Adapter<ChatListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatlist, parent, false)

        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val data = dataSet[position]
        holder.chatId.text = data.chatId
        holder.chatName.text = data.chatName

        holder.linearLayout.setOnClickListener {
            Toast.makeText(context, "Clicked name: "+ holder.chatId.text.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view){
    val linearLayout: LinearLayout = view.findViewById(R.id.chatLinearLayout)
    val chatName: TextView = view.findViewById(R.id.chatName)
    val chatId: TextView = view.findViewById(R.id.chatId)
}