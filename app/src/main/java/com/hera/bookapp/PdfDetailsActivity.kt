package com.hera.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.ActivityPdfDetailsBinding

class PdfDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfDetailsBinding

    private var bookId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId=intent.getStringExtra("bookId")!!

        //increment book view count, whhenever this page starts
        MyApplication.incrementBookViewCount(bookId)
        loadBookDetails()
        //handle backbutton
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.readBookBtn.setOnClickListener {
            //handle click open pdf view activity
            val intent=Intent(this,PdfViewActivity::class.java)
            intent.putExtra("bookId",bookId)
            startActivity(intent)
        }
    }

    private fun loadBookDetails() {
        //Books > bookId > Details
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get data
                    val categoryId=""+ snapshot.child("categoryId").value
                    val description=""+ snapshot.child("description").value
                    val dowloadcount=""+ snapshot.child("dowloadsCount").value
                    val timestamp=""+ snapshot.child("timestamp").value
                    val title=""+ snapshot.child("title").value
                    val uid=""+ snapshot.child("uid").value
                    val url=""+ snapshot.child("url").value
                    val viewsCount=""+ snapshot.child("viewsCount").value

                    //format date
                    val date=MyApplication.formatTimeStamp(timestamp.toLong())
                    // load pdf category
                    MyApplication.loadCategory(categoryId,binding.categoryEt)

                    //load pdf thumbnail, pages count
                    MyApplication.loadPdfFromUrlSinglePage(url,title,binding.pdfview,binding.prgreesbar,binding.pagestv)
                    //load pdf size
                    MyApplication.loadPdfSiz(url,title,binding.sizeTv)

                    //set data
                    binding.titleTv.text=title
                    binding.descriptionsTv.text=description
                    binding.viewstv.text=viewsCount
                    binding.dowloadstv.text=dowloadcount
                    binding.dateTv.text=date

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PdfDetailsActivity,"Failed not show details ${error.message}",Toast.LENGTH_SHORT).show()
                }

            })
    }
}