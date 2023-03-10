package com.hera.bookapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hera.bookapp.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Please Wait...")
        progressBar.setCanceledOnTouchOutside(false)


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.submitBtn.setOnClickListener {
            validateData()
        }


    }
    private var category=""
    @SuppressLint("SuspiciousIndentation")
    private fun validateData() {
    category = binding.categoryEt.text.toString()

        if (category.isEmpty()){
            Toast.makeText(this,"Enter Category...",Toast.LENGTH_SHORT).show()
        }else{
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        progressBar.show()

        val timestamp= System.currentTimeMillis()

        val hashMap= HashMap<String,Any?>()
        hashMap["id"] = "$timestamp"
        hashMap["category"]= category
        hashMap["timestamp"]=timestamp
        hashMap["uid"]="${firebaseAuth.uid}"

        val ref =FirebaseDatabase.getInstance().getReference("Category")
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                //added succesfull
                progressBar.dismiss()
                Toast.makeText(this,"Added Successfully Category...",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                progressBar.dismiss()
                Toast.makeText(this,"Failed to add due to ${e.message}.",Toast.LENGTH_SHORT).show()
            }
    }
}