package com.example.chatterbox

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater

import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ColorStateListInflaterCompat.inflate

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatterbox.adapters.MessageAdapter
import com.example.chatterbox.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.*
import java.util.zip.Inflater
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth



    var receiverRoom : String?= null  //used to create unique room for sender and receiver
    //so that message is private and is not reflected in every chat
    var senderRoom : String?= null
    var a: String? = null



    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        a= receiverUid


        receiverRoom = receiverUid + senderUid
        senderRoom = senderUid + receiverUid

        supportActionBar?.title = name

        messageBox = findViewById(R.id.et_send)
        sendButton = findViewById(R.id.btn_send)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth= FirebaseAuth.getInstance()

        //logic to add data to recycler view from firebase
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)

                        messageList.add(message!!)

                        //scroll to the last item on data change ___wow__
                        chatRecyclerView.post(Runnable { chatRecyclerView
                            .smoothScrollToPosition(messageAdapter.itemCount - 1) })

                    }
                    messageAdapter.notifyDataSetChanged()



                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //adding the message to the database
        sendButton.setOnClickListener{

            //tvdateTime?.text = SimpleDateFormat("dd MMM hh:mm a").format(Calendar.getInstance().time)
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            messageBox.setText("")
        }


    }
}