package xyz.moshimoshi.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import xyz.moshimoshi.R
import xyz.moshimoshi.adapters.ChatListAdapter
import xyz.moshimoshi.models.ChatList

class HomeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        val refreshLayout: SwipeRefreshLayout = view.findViewById(R.id.homeSwipeRefreshLayout)
        refreshLayout.setOnRefreshListener {
            val thread = Thread(){
                run{
                    Thread.sleep(2000)

                    refreshLayout.isRefreshing = false
                }
            }
            thread.start()
        }

        return view
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

        recyclerView?.adapter = ChatListAdapter(requireContext(), chatLists)
        recyclerView?.layoutManager = layoutManager
    }
}