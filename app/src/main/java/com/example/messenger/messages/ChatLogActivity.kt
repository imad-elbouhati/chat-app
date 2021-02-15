package com.example.messenger.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.NewMessageActivity.Companion.USER_KEY
import com.example.messenger.R
import com.example.messenger.UserItem
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.from_message_item.view.*
import kotlinx.android.synthetic.main.to_message_item.view.*


class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    private  val TAG = "ChatLogActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val user = intent.getSerializableExtra(USER_KEY) as? User
        supportActionBar?.title=user?.username
        recycler_view_chat_log.adapter = adapter
        //setupDummyData()
        listenForMessages()
        button_send_chat_log.setOnClickListener {
            Log.d(TAG, "onCreate: send button clicked")
            performSendMessage()
        }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                Log.d(TAG, "onChildAdded: $chatMessage")
                if (chatMessage == null) return
                if (FirebaseAuth.getInstance().uid == chatMessage.fromId) {
                    adapter.add(ItemTo(chatMessage.text))
                } else {
                    adapter.add(ItemFrom(chatMessage.text))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun performSendMessage() {
        val text = edit_text_send_message_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getSerializableExtra(USER_KEY) as? User
        val toId = user?.uid
        if(fromId==null)return
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG, "performSendMessage: message saved ${ref.key}")
        }
    }

    private fun setupDummyData() {
        val adapter = GroupAdapter<GroupieViewHolder>()

            adapter.add(ItemFrom("Test test\nTest Test"))
            adapter.add(ItemTo("Hello Howe r u ?"))
            adapter.add(ItemFrom("Test test\nTest Test"))
            adapter.add(ItemTo("Hello Howe r u ?"))
            adapter.add(ItemFrom("Test test\nTest Test"))
            adapter.add(ItemTo("Hello Howe r u ?"))
            recycler_view_chat_log.adapter = adapter
    }
}

class ItemFrom(val text:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.from_message_chat_log.text=text
    }

    override fun getLayout(): Int {
        return R.layout.from_message_item
    }

}
class ItemTo(val text:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.to_message_chat_log.text=text

    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}
