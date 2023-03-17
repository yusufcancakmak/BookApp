package com.hera.bookapp

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hera.bookapp.databinding.ActivityPdfDetailsBinding
import java.io.FileOutputStream

class PdfDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfDetailsBinding
    private companion object{
         const val TAG="BOOK_DETAİLS_TAG"
    }
    private lateinit var progressDialog:ProgressDialog

    private var bookId=""
    //get from firebase
    private var bookTitle=""
    private var bookUrl=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId=intent.getStringExtra("bookId")!!

        progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

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

        //handle click, dowlload book/pdf
        binding.dowloadBTN.setOnClickListener {
        if (ContextCompat.checkSelfPermission(this@PdfDetailsActivity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"onCreate: Stroge Permission is already granted")
            downloadBook()
        }
            else{
                Log.d(TAG,"onCreate: Stroage Permission not granted")
            requestStroagePermissioLaucher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        }
    }
    private val requestStroagePermissioLaucher= registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted:Boolean->
        if (isGranted){
            Log.d(TAG,"onCreate: Storage permission is granted")
            downloadBook()
        }
        else{
            Log.d(TAG,"onCreate: Stroage Permission not granted")
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
        }

    }
    private fun downloadBook(){

            progressDialog.setMessage("Download Book")

                progressDialog.show()
        //download book firebase stroage using url
        val storageRefrance=FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageRefrance.getBytes(Constants.Max_Bytes_pdf)
            .addOnSuccessListener {bytes->
                Log.d(TAG,"downloadBook: Book downloaded...")
                saveToDownloadsFolder(bytes)

            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.d(TAG,"DownloadBokk: download is failed${it.message}")
                Toast.makeText(this,"DownloadBokk: download is failed${it.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun saveToDownloadsFolder(bytes:ByteArray?) {
        Log.d(TAG,"downloadBook: saving downloaded...")

        val nameWithExtentition="${System.currentTimeMillis()}.pdf"
        try {
            val downloaderFolder =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloaderFolder.mkdirs()

            val filePath=downloaderFolder.path+"/"+nameWithExtentition

            val out=FileOutputStream(filePath)
            out.write(bytes)
            out.close()

            Toast.makeText(this,"Saved to downloads Folder",Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            incrementDownloadCount()
        }catch (e:java.lang.Exception){
            progressDialog.dismiss()
            Log.d(TAG,"saveToDownloadFolder:Filed to save due to${e.message}")
            Toast.makeText(this,"Failed to save due to ${e.message}",Toast.LENGTH_SHORT).show()

        }
    }
        //gpt
    private fun incrementDownloadCount() {
        //increment doenloads count to firebase db
        Log.d(TAG,"incrementDownloadCount: ")
        //get previos downloads count

        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get downloads count
                    var downloadsCout="${snapshot.child("downloadsCount").value}"
                    Log.d(TAG,"OnDatachange: Current Downloads Count: $downloadsCout")
                    if (downloadsCout ==""||downloadsCout =="null"){
                        downloadsCout="0"
                    }
                    //convert to long and increment
                    val newDownloadCount:Long = downloadsCout.toLong()+1
                    Log.d(TAG,"OnDatachange: new Downloads Count: $downloadsCout")
                    //setup
                    val hashmap =HashMap<String,Any>()
                    hashmap["downloadsCount"]=newDownloadCount
                    //update new increment downloads count
                    val dbref=FirebaseDatabase.getInstance().getReference("Books")
                    dbref.child(bookId)
                        .updateChildren(hashmap)
                        .addOnSuccessListener {
                        Log.d(TAG,"onDataChange: Downloads count incremented")
                        }
                        .addOnFailureListener { e->
                            Log.d(TAG,"onDataChange: Failed to increment due to ${e.message}")
                        }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
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
                    bookTitle=""+ snapshot.child("title").value
                    val uid=""+ snapshot.child("uid").value
                    bookUrl=""+ snapshot.child("url").value
                    val viewsCount=""+ snapshot.child("viewsCount").value

                    //format date
                    val date=MyApplication.formatTimeStamp(timestamp.toLong())
                    // load pdf category
                    MyApplication.loadCategory(categoryId,binding.categoryEt)

                    //load pdf thumbnail, pages count
                    MyApplication.loadPdfFromUrlSinglePage(bookUrl,bookTitle,binding.pdfview,binding.prgreesbar,binding.pagestv)
                    //load pdf size
                    MyApplication.loadPdfSiz(bookUrl,bookTitle,binding.sizeTv)

                    //set data
                    binding.titleTv.text=bookTitle
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