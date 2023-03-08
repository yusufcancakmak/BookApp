package com.hera.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        //init progress dialog,will show
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            validateData()
        }


    }
    private var email=""
    private var password=""
    private fun validateData() {
        email=binding.emailTxt.text.toString().trim()
        password=binding.passwordTxt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalited email format",Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show()

        }
        else{
            loginUser()
        }

    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()

            }
            .addOnFailureListener {e->
            Toast.makeText(this,"Login failed due to ${e.message.toString()}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")

        val firebaseUser= firebaseAuth.currentUser

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object  :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                val userType = snapshot.child("userType").value
                if (userType=="user"){
                    startActivity(Intent(this@LoginActivity,DashBoardUserActivity::class.java))
                    finish()
                }
                else if (userType=="admin")
                    startActivity(Intent(this@LoginActivity,DashboardAdminActivity::class.java))
                finish()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}