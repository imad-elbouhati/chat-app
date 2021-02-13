package com.example.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.NewMessageActivity
import com.example.messenger.NewMessageActivity.Companion.USER_KEY
import com.example.messenger.R
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.user_item_list.view.*
import kotlinx.android.synthetic.main.from_message_item.view.*
import kotlinx.android.synthetic.main.activity_chat_log.view.*
import kotlinx.android.synthetic.main.from_message_item.view.from_message_circle_image
import kotlinx.android.synthetic.main.to_message_item.view.*


class ChatLogActivity : AppCompatActivity() {
    private  val TAG = "ChatLogActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val user = intent.getSerializableExtra(USER_KEY) as? User
        var profileImageUrl=""
        val ref = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseDatabase.getInstance().reference
        db.child("users").child(ref!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                      profileImageUrl = snapshot.child("profileImageUrl").value!!.toString()
                    Log.d(TAG, "onDataChange: $profileImageUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )

        supportActionBar?.title=user?.username
        val adapter = GroupAdapter<GroupieViewHolder>()

        if(user!=null){
            adapter.add(ItemFrom(user))
            adapter.add(ItemFrom(user))
            adapter.add(ItemTo(profileImageUrl))
            adapter.add(ItemFrom(user))
            adapter.add(ItemTo(profileImageUrl))
            adapter.add(ItemFrom(user))
            adapter.add(ItemFrom(user))
            adapter.add(ItemTo(profileImageUrl))
        }


        recycler_view_chat_log.adapter = adapter
    }
}

class ItemFrom(val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply{
            Picasso.get().load(user.profileImageUrl).into(from_message_circle_image)
        }

    }

    override fun getLayout(): Int {
        return R.layout.from_message_item
    }

}
class ItemTo(val url:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if(url != ""){
            viewHolder.itemView.apply{
                Picasso.get().load(url).into(to_message_circle_image)
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}
