package xyz.moshimoshi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.moshimoshi.R
import xyz.moshimoshi.models.Message

class MessageAdapter(val context: Context, private val messages: ArrayList<Message>, private val senderId: String): RecyclerView.Adapter<MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val data = messages[position]
        val id = data.senderId

        holder.rightView.visibility = View.INVISIBLE
        holder.leftView.visibility = View.INVISIBLE
        holder.leftImage.visibility = View.INVISIBLE

        if(senderId == id){
            holder.rightView.text = data.message
            holder.rightView.visibility = View.VISIBLE
        } else {
            holder.leftView.text = data.message
            holder.leftView.visibility = View.VISIBLE
            holder.leftImage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

}

class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val leftImage: ImageView = view.findViewById(R.id.itemMessageLeftImage)
    val leftView: TextView = view.findViewById(R.id.itemMessageLeft)
    val rightView: TextView = view.findViewById(R.id.itemMessageRight)
}