package com.example.chatterbox

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatterbox.models.User
import com.example.chatterbox.adapters.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()

        adapter = UserAdapter(this, userList)

        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mDbRef.child("User").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            mAuth.signOut()

            val intent = Intent(this, Signin::class.java)
            finish()
            startActivity(intent)

            return true
        }
        if (item.itemId == R.id.change_name){

            val dialog = AlertDialog.Builder(this@MainActivity)
            val inflate = layoutInflater.inflate(R.layout.update_name_user, null)
            val editText: EditText = inflate.findViewById(R.id.et_update_name)
            with(dialog){
                setTitle("Update your Name")
                setPositiveButton("Update"){dialog, which ->
                    mDbRef.child("User").child(mAuth.currentUser!!.uid)
                        .child("name").setValue(editText.text.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this@MainActivity, "Name : ${editText.text}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@MainActivity, "some error occurred", Toast.LENGTH_SHORT).show()
                        }
                }
                setNegativeButton("Cancel"){ dialog, which ->
                    Log.d("ChatActivity", "Negative button clicked" )
                }
                setView(inflate)
                show()

            }
        }
        return true
    }
}