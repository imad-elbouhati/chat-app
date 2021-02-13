package com.example.messenger

import android.content.Intent
import android.icu.number.NumberFormatter.with
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.messages.ChatLogActivity
import com.example.messenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_item_list.view.*

class NewMessageActivity : AppCompatActivity() {
    private  val TAG = "NewMessageActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title="Select User"

        val adapter = GroupAdapter<GroupieViewHolder>()

        fetchUsers()
    }
    companion object{
        const val USER_KEY="user"
    }
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    Log.d(TAG, "onDataChange: $user")
                    adapter.add(UserItem(user!!))
                }
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    val userItem = item as UserItem
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recycler_new_message.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}

class UserItem(val user:User): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            username_new_message.text = user.username
            Picasso.get().load(user.profileImageUrl).into(circleImageView)

        }
    }

    override fun getLayout(): Int {
        return R.layout.user_item_list
    }

}
