package com.hera.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hera.bookapp.databinding.ActivityPdfViewBinding


class PdfViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewBinding
    //book id
    private var bookId = ""
    private companion object {
        private const val TAG = "PDF_VIEW_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //get book id from intent it will be used to load book from firebase
        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        Log.d(TAG, "LoadbookDetails: et Pdf Url from db")
        //database referance to get details  get book url using book id
        //step get url using Id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            //sor
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book url
                    val pdfUrl = "" + snapshot.child("url").value
                    Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                    loadBookFromUrl(pdfUrl)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get Pdf from firebase storage using URL")
        val referance = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        referance.getBytes(
            Constants.Max_Bytes_pdf
        )
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: pdf got from url")
                binding.pdfview.fromBytes(bytes) // set false to scroll vertical, set true to scroll horiztal
                    .swipeHorizontal(false)//set false to scroll vertical,
                    .onPageChange { page, pageCount ->
                        val currentPage = page + 1 //page starts from  so do +1 to start from 1
                        binding.toolbarSubtitleTv.text = "$currentPage/$pageCount" // 3/222
                        Log.d(TAG, "LoadBookFromUrl:$currentPage/$pageCount ")
                    }
                    .onError { t ->
                        Log.d(TAG, "LoadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "LoadBookFromUrl: ${t.message}")
                    }
                    .load()
                binding.progressbar.visibility = View.GONE

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "LoadBookFromUrl: Failed to get url due to ${e.message}")
                binding.progressbar.visibility = View.GONE
            }
    }
}