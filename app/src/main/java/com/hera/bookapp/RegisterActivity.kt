package com.hera.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hera.bookapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog
    private  var name=""
    private  var email=""
    private  var password=""
    private  var confirmPassord=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        //init progress dialog,will show
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener { onBackPressed() }

        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
         name =binding.nameTxt.text.toString().trim()
         email =binding.emailTxt.text.toString().trim()
         password=binding.passwordTxt.text.toString().trim()
         confirmPassord=binding.confirmPasswordTxt.text.toString().trim()

        if (name.isEmpty()){
            Toast.makeText(this,"Enter your Name....",Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Pattern....",Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this,"Enter Password...",Toast.LENGTH_SHORT).show()
        }
        else if (confirmPassord.isEmpty()){
            Toast.makeText(this,"Confirm Password....",Toast.LENGTH_SHORT).show()
        }
        else if(password!=confirmPassord){
            Toast.makeText(this,"Password doesn't match....",Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
      //show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password).
        addOnSuccessListener {
            updateUserInfo()

        }.addOnFailureListener{ e->
            progressDialog.dismiss()
            Toast.makeText(this,"Failed creating account due to ${e.message.toString()}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user info...")

        val timestamp =System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"]= "user"
        hashMap["timestamp"]=timestamp

        //set data to db

        val ref =FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Account created....",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashBoardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed savering user info due to ${e.message.toString()}",Toast.LENGTH_SHORT).show()

            }
    }
}