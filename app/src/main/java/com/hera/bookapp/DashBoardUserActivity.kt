package com.hera.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hera.bookapp.databinding.ActivityDashBoardBinding

class DashBoardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.lagoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        // get current user
        val firebaseuser = firebaseAuth.currentUser
        if (firebaseuser==null){
            // not logged in, user can stay in user dashboarf without login
            binding.subTitleTv.text="Not Logged In"


        }else{
            val email=firebaseuser.email
            binding.subTitleTv.text=email

        }
    }
}