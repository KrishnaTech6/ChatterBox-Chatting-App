package com.example.chatterbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatterbox.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var name: EditText
    lateinit var password: EditText
    lateinit var signup: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        name = findViewById(R.id.et_name)
        email = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        signup = findViewById(R.id.btn_signup)

        signup.setOnClickListener {

//            val name = name.text.toString()
            val name = name.text.toString()
            val email = email.text.toString()
            val password = password.text.toString()

            if (email=="" || password==""){
                Toast.makeText(this,"Field Can't be Empty", Toast.LENGTH_SHORT).show()
            }

            signUp( name, email, password)

        }
    }

    private fun signUp(name: String, email:String, password: String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //jumping to home activity

                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!)

                    val intent = Intent(this@Signup, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Signup, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String){
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("User").child(uid).setValue(User(name, email, uid))
    }




}