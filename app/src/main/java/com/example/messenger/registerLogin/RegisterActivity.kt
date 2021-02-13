package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.messenger.messages.LatestMessagesActivity
import com.example.messenger.models.User
import com.example.messenger.registerLogin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_registration.setOnClickListener {
            performRegister()
        }
        already_have_acccount_text_view.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        select_image_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
    var selectedPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            Log.d(TAG, "onActivityResult: photo selected")
            selectedPhotoUri = data.data
            Log.d(TAG, "onActivityResult: photo Uri: $selectedPhotoUri")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_image_view.setImageBitmap(bitmap)
            select_image_button_register.alpha=0f
        }
    }

    private fun performRegister() {
        val username = username_registration_edit_text.text.toString()
        val email = email_registration_edit_text.text.toString()
        val password = password_registration_edit_text.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter email/pw",Toast.LENGTH_LONG).show()
            return
        }
        Log.d(TAG, "onCreate: $username $email $password")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful)return@addOnCompleteListener
                    Log.d(TAG, "onCreate: ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener{
                    Log.d(TAG, "onCreate: ${it.message}")
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri==null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { it ->
                Log.d(TAG, "uploadImageToFirebaseStorage: ${it.metadata?.path}")
              ref.downloadUrl.addOnSuccessListener {
                  Log.d(TAG, "uploadImageToFirebaseStorage: downloadUrl ${it}")
                  saveUserToFirebaseDatabase(it.toString())
              }
            }.addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_registration_edit_text.text.toString(),profileImageUrl)
        ref.setValue(user).addOnSuccessListener {
            Toast.makeText(this,"user saved",Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }.addOnFailureListener{
            Toast.makeText(this,"user not saved",Toast.LENGTH_SHORT)
                .show()
            Log.d(TAG, "saveUserToFirebaseDatabase: ${it.message}")
        }
    }
}

