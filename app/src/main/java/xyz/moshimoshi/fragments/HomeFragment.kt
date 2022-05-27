package xyz.moshimoshi.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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

        val senderId = Firebase.auth.currentUser!!.uid
        adapter = ChatListAdapter(requireActivity(), chatLists, senderId)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = layoutManager

        refreshLayout = view.findViewById(R.id.homeSwipeRefreshLayout)
        refreshLayout.setOnRefreshListener {
            loadMessages()
        }

        registerMessageListener()
        loadMessages()

        return view
    }

    private fun registerMessageListener(){
        val user = Firebase.auth.currentUser!!
        val database = Firebase.firestore
        database.collection("messages").whereEqualTo("senderId", user.uid)
            .addSnapshotListener { snapshots, e ->
                snapshotListener(snapshots, e)
            }

        database.collection("messages").whereEqualTo("receiverId", user.uid)
            .addSnapshotListener { snapshots, e ->
                snapshotListener(snapshots, e)
            }
    }

    private fun snapshotListener(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?){
        if(e != null){
            Log.e("MessageListener", "Failed to listen")
            return
        }
        if(snapshots != null){
            val receivedDocuments = snapshots.documentChanges
            receivedDocuments.forEach { datas ->
                if(datas.type == DocumentChange.Type.ADDED) {
                    val receivedData = datas.document.data
                    for (chatList in chatLists) {
                        if(chatList.chatId == receivedData["chats_id"]){
                            chatList.chatLastMessage = receivedData["message"] as String
                            chatList.chatLastMessageBy = receivedData["senderId"] as String
                            chatList.chatLastMessageTimestamp = receivedData["timestamp"] as Long

                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
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
                    val chatUsers = result.data as Map<*, *>

                    if (chatUsers.isEmpty()) {
                        Toast.makeText(context, "No messages found!", Toast.LENGTH_SHORT).show()
                        refreshLayout.isRefreshing = false
                    } else {
                        chatLists.clear()
                        chatUsers.forEach { chatDetails ->
                            val userId = chatDetails.key
                            val chatId = chatDetails.value
                            val userDetail = database.collection("users").document(userId.toString()).get()

                            userDetail.addOnCompleteListener { userData ->
                                if (userData.isSuccessful) {
                                    val users: ArrayList<String> = ArrayList()
                                    val userResult = userData.result
                                    val name = userResult.get("username")

                                    users.add(userId.toString())

                                    val chatDetail = database.collection("chats").document(chatId.toString()).get()

                                    chatDetail.addOnCompleteListener { chats ->
                                        if(chats.isSuccessful){
                                            if(chats.result.data != null) {
                                                val resultData = chats.result.data!!
                                                val chatLastMessage = resultData["lastMessage"] as String
                                                val chatLastMessageBy = resultData["lastMessageBy"] as String
                                                val chatLastMessageTimestamp = resultData["lastMessageTimestamp"] as Long
                                                val chatList = ChatList(chatId.toString(), name.toString(), users, chatLastMessage, chatLastMessageBy, chatLastMessageTimestamp)

                                                chatLists.add(chatList)
                                                chatLists.sortByDescending { chat -> chat.chatLastMessageTimestamp }

                                                adapter.notifyDataSetChanged()
                                            }
                                        } else {
                                            Toast.makeText(context, "Something went wrong! Couldn't load messages!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
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