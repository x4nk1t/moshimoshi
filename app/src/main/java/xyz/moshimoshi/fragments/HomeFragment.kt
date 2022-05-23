package xyz.moshimoshi.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.moshimoshi.R
import xyz.moshimoshi.adapters.ChatListAdapter
import xyz.moshimoshi.models.ChatList

class HomeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMessages()
    }

    private fun loadMessages(){
        val recyclerView: RecyclerView? = view?.findViewById(R.id.chatListRecyclerView)
        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = LinearLayoutManager.VERTICAL

        val chatLists: ArrayList<ChatList> = ArrayList()
        val users: ArrayList<String> = ArrayList()
        users.add("user1")
        users.add("user2")

        chatLists.add(ChatList("John", users, "You: I am GOD!", "user1"))
        chatLists.add(ChatList("Jack", users, "Jack: I am not GOD!", "user2"))

        recyclerView?.adapter = ChatListAdapter(chatLists)
        recyclerView?.layoutManager = layoutManager
    }
}