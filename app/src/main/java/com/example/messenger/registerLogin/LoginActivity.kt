package com.example.messenger.registerLogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener {
            val email = email_login_edit_text.text.toString()
            val password = password_login_edit_text.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful)return@addOnCompleteListener
                    Log.d("MainActivity", "Login Successfully: ${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Log.d("MainActivity", "Login Failed: ${it.message}")
                }
        }
        back_to_register_text_view.setOnClickListener {
            finish()
        }
    }
}