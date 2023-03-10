package com.hera.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList:ArrayList<ModelCategory>
    private lateinit var adapterCategory: AdapterCategory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()
        //search
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               // called as and when user anyting
                try {
                adapterCategory.filter.filter(s)
                }catch (e:java.lang.Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.lagoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        //click start add category page
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this,CategoryActivity::class.java))
        }
    }

    private fun loadCategories() {
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Category")
        ref.addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               // clear list before starting adding into it
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    //get datta as model

                    val model =ds.getValue(ModelCategory::class.java)
                    //add to arraylist
                    categoryArrayList.add(model!!)

                }
                adapterCategory=AdapterCategory(categoryArrayList)
                //set adapter to recyvler
                binding.categoreisRv.adapter=adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

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