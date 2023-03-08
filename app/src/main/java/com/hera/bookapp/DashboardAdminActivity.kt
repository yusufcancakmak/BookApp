package com.hera.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hera.bookapp.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.lagoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
      // get current user
        val firebaseuser = firebaseAuth.currentUser
        if (firebaseuser==null){
            // not logged in, go to main
            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }else{
            val email=firebaseuser.email
            binding.subTitleTv.text=email

        }
    }
}