package com.example.chatterbox

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Signin : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var signin: Button
    lateinit var signup: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser?.uid != null){
            //if logged in, directly go to main activity
            //else sign in
            val intent = Intent(this@Signin, MainActivity::class.java)
            finish() // if back pressed it should not go to sign in activity , therefore we have to add finish()
            startActivity(intent)
        }
        else{

            email = findViewById(R.id.et_email)
            password = findViewById(R.id.et_password)
            signup = findViewById(R.id.btn_signup)
            signin = findViewById(R.id.btn_signin)

            signup.setOnClickListener {
                val intent = Intent(this@Signin, Signup::class.java)
                startActivity(intent)
            }

            signin.setOnClickListener {
                val email = email.text.toString()
                val password = password.text.toString()

                login(email, password)
            }

        }


    }
    private fun login(email: String, password: String){

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, going to main activity
                    val intent = Intent(this@Signin, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Signin,"User doesn't Exist", Toast.LENGTH_SHORT).show()
                }
            }


    }


}