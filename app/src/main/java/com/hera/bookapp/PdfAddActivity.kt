package com.hera.bookapp

import android.app.AlertDialog
import android.app.Application
import android.app.Instrumentation.ActivityResult
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hera.bookapp.databinding.ActivityPdfAddBinding
import java.net.URI
import kotlin.math.log

class PdfAddActivity : AppCompatActivity() {
    //viewbinding
    //firebase auth
    //progress dialog
    //arraylist
    //uri of picked

    private lateinit var binding: ActivityPdfAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private var pdfUri: Uri? =null
    //Tag
    private val TAG="PDF_ADD_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        loadCategories()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click back goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        // handle click, show category pick dialog
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }
        //handle click, pick pdf itent
        binding.attachBtn.setOnClickListener {
            pdfPickIntent()
        }
        binding.submitBtn.setOnClickListener {
            //validate date
            //upload pdf to firebase storage
            // get url of upload pdf
            //upload pdf info to firebadase db

            validateData()
        }
    }
    private var title =""
    private var description =""
    private var category = ""
    private fun validateData() {
        Log.d(TAG,"VALİDATEDATA:VALİDATİNG DATA")

        title=binding.titleEt.text.toString().trim()
        description=binding.descriptionEt.text.toString().trim()
        category=binding.categoryTv.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this,"Enter title...",Toast.LENGTH_SHORT).show()
        }else if (description.isEmpty()){
            Toast.makeText(this,"Enter description...",Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()){
            Toast.makeText(this,"Pick category...",Toast.LENGTH_SHORT).show()
        }
        else if (pdfUri == null){
            Toast.makeText(this,"Pick Pdf...",Toast.LENGTH_SHORT).show()
        }
        else{
            // data validated,begin upload
            uploadPdfToStroage()
        }


    }

    private fun uploadPdfToStroage() {
        Log.d(TAG,"UploadPdfToStorage: Uploading to storage...")

        progressDialog.setMessage("uploading PDF...")
        progressDialog.show()

        //timestamp
        val timestamp = System.currentTimeMillis()
        //path of pdf in firebase storage
        val filePathAndName ="Books/$timestamp"
        //stroage ref
        val storageReferance=FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReferance.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot->
                Log.d(TAG,"Upoad to pdf uploaded now getting url..")

                //get url of uploaded pdf
                val uritask:Task<Uri> =taskSnapshot.storage.downloadUrl
                while (!uritask.isSuccessful);
                val uploadedPdfUrl = "${uritask.result}"

                uploadedPdfInfoToDb(uploadedPdfUrl,timestamp)
            }
            .addOnFailureListener{ e->
                Log.d(TAG,"UploadPdfToStroage: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload due to${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadedPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
        //Upload Pdf info to firebase db
        Log.d(TAG,"uploadedPdfInfoDb:Uploaded db")
        progressDialog.setMessage("Uploading pdf info...")

        //uid of current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap:HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewCount"] = 0
        hashMap["downloadsCount"] = 0

        //db referance db > books>bookıd>(bookınfo)
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"uploadedPdfInfoToDb:UPLOADED")
                progressDialog.dismiss()
                Toast.makeText(this,"Uploaded....",Toast.LENGTH_SHORT).show()
                pdfUri ==null
            }
            .addOnFailureListener {e->
                Log.d(TAG,"uploadedPdfInfoToDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload due to${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun loadCategories() {
        Log.d(TAG,"LoadPdfCategories: Loading pdf categories")
        categoryArrayList = ArrayList()

        val ref=FirebaseDatabase.getInstance().getReference("Category")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to arraylist
                    categoryArrayList.add(model!!)
                    Log.d(TAG,"OnDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private var selectedCategoryId =""
    private var selectedCategoryTitle =""
    private fun categoryPickDialog(){
        Log.d(TAG, "CategoryPickDialog: Showing PDF category pick dialog ")

        val categoriesArraylist = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoriesArraylist.indices){
            categoriesArraylist[i]=categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArraylist){dialog, wich->
                //handle item click
                //get clicked item

                selectedCategoryTitle=categoryArrayList[wich].category
                selectedCategoryId=categoryArrayList[wich].id
                //
                binding.categoryTv.text=selectedCategoryTitle
                Log.d(TAG,"CategoryPickDialog: Selected Category ID: $selectedCategoryId")
                Log.d(TAG,"CategoryPickDialog: Selected Category ID: $selectedCategoryTitle")
            }
            .show()
    }
    private fun pdfPickIntent(){
        Log.d(TAG,"PdfPickIntent: starting pdf pick intent")
         val intent =Intent()
        intent.type="application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    val pdfActivityResultLauncher=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<androidx.activity.result.ActivityResult>{result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG,"PDF PİCKED")
                pdfUri=result.data!!.data
            }
            else{
                Log.d(TAG,"PDF Pick cancelled")
                Toast.makeText(this, "Cancelled ",Toast.LENGTH_SHORT).show()
            }

        }
    )


}