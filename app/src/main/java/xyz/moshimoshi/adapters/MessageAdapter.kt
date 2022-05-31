package xyz.moshimoshi.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
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
        val message = parseMessage(data.message!!)

        holder.rightView.visibility = View.INVISIBLE
        holder.leftView.visibility = View.INVISIBLE
        holder.leftImage.visibility = View.INVISIBLE

        if(senderId == id){
            holder.rightView.text = message
            holder.rightView.movementMethod = LinkMovementMethod.getInstance()
            holder.rightView.visibility = View.VISIBLE
        } else {
            holder.leftView.text = message
            holder.rightView.movementMethod = LinkMovementMethod.getInstance()
            holder.leftView.visibility = View.VISIBLE
            holder.leftImage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun parseMessage(message: String): SpannableString{
        val messageSplit = message.split(" ")
        val spannable = SpannableString(message)

        for (mess in messageSplit) {
            val links = Patterns.WEB_URL.matcher(mess).matches()

            if (links){
                val clickable = object: ClickableSpan() {
                    override fun onClick(view: View) {
                        var clickedLink = Uri.parse(mess)

                        if (!mess.startsWith("http://") && !mess.startsWith("https://")) {
                            clickedLink = Uri.parse("http://$mess")
                        }

                        context.startActivity(Intent(Intent.ACTION_VIEW, clickedLink))
                    }
                }

                val start = message.indexOf(mess)
                val end = start + mess.length
                spannable.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.linkColor)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return spannable
    }

}

class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val leftImage: ImageView = view.findViewById(R.id.itemMessageLeftImage)
    val leftView: TextView = view.findViewById(R.id.itemMessageLeft)
    val rightView: TextView = view.findViewById(R.id.itemMessageRight)
}