package xyz.moshimoshi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import xyz.moshimoshi.R
import xyz.moshimoshi.activities.NewMessageActivity
import xyz.moshimoshi.adapters.ChatListAdapter
import xyz.moshimoshi.models.ChatList

class HomeFragment: Fragment() {
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var adapter: ChatListAdapter
    private var chatLists: ArrayList<ChatList> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView: RecyclerView? = view?.findViewById(R.id.chatListRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = ChatListAdapter(requireContext(), chatLists)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = layoutManager

        refreshLayout = view.findViewById(R.id.homeSwipeRefreshLayout)
        refreshLayout.setOnRefreshListener {
            loadMessages()
        }

        loadMessages()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: FloatingActionButton = requireView().findViewById(R.id.new_message_button)

        fab.setOnClickListener {
            val newMessageIntent = Intent(context, NewMessageActivity::class.java)
            startActivity(newMessageIntent)
        }
    }

    private fun loadMessages(){
        refreshLayout.isRefreshing = true

        val database = Firebase.firestore
        val user = Firebase.auth
        val chatBoxes = database.collection("chatbox").document(user.uid!!).get()

        chatBoxes.addOnCompleteListener(requireActivity()) {
            if(it.isSuccessful){
                if(it.result.data == null){
                    Toast.makeText(context, "No messages found!", Toast.LENGTH_SHORT).show()
                    refreshLayout.isRefreshing = false
                } else {
                    val result = it.result
                    val chatUsers = result.data?.get("chats") as Map<*, *>

                    if (chatUsers.isEmpty()) {
                        Toast.makeText(context, "No messages found!", Toast.LENGTH_SHORT).show()
                    } else {
                        chatLists.clear()
                        chatUsers.forEach { chatDetails ->
                            val userId = chatDetails.key
                            val chatId = chatDetails.value
                            val userDetail =
                                database.collection("users").document(userId.toString()).get()

                            userDetail.addOnCompleteListener(requireActivity()) { userData ->
                                if (userData.isSuccessful) {
                                    val users: ArrayList<String> = ArrayList()
                                    val userResult = userData.result
                                    val name = userResult.get("username")

                                    val chatList =
                                        ChatList(chatId.toString(), name.toString(), users)

                                    users.add(userId.toString())
                                    users.add(user.uid.toString())

                                    chatLists.add(chatList)
                                    adapter.notifyDataSetChanged()
                                }

                                if (userId == chatUsers.keys.last()) {
                                    refreshLayout.isRefreshing = false
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Couldn't load messages!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}