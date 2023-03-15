package com.hera.bookapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.ZygotePreload
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.ActivityPdfEditBinding

class PdfEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfEditBinding
    private companion object{
        private const val TAG="pDF_LİST"
    }

    private var bookId=""
    private lateinit var progressDialog:ProgressDialog
    //arraylist to hold category title
    private lateinit var categoryTitleArrayList: ArrayList<String>
    //arraylist to category ids
    private lateinit var categoryIdArrayList:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
            //get book id edit
        bookId=intent.getStringExtra("bookId")!!

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(true)

        loadCategories()
        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }
        binding.submitBtn.setOnClickListener {
            validateData()

        }
    }
    private var title =""
    private var description =""

    private fun validateData() {
    //get data
        title=binding.titleEt.text.toString().trim()
        description=binding.descriptionEt.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this,"Enter title",Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this,"Enter Descriptions",Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty()){
            Toast.makeText(this,"Pick Category",Toast.LENGTH_SHORT).show()
        }
        else{
            updatePdf()
        }
    }

    private fun updatePdf() {
        Log.d(TAG,"updatePdf: Starting updating pdf info...")

        //show prgoresss
        progressDialog.setMessage("updating book info")
        progressDialog.show()

        //setup data to update to db

        val hashmap=HashMap<String,Any>()
        hashmap["title"]="$title"
        hashmap["description"]="$description"
        hashmap["categoryId"]="$selectedCategoryId"

        //start updating
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashmap)
            .addOnSuccessListener {
                Log.d(TAG,"UpdatePdf: Updated successfully...")
                progressDialog.dismiss()
                Toast.makeText(this,"Updated succesfully...",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                Log.d(TAG,"Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to update due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId=""
    private var selectedCategoryTitle=""
    private fun categoryDialog() {
        //show dialog to pick the category of/book we got the categories
        //make string array from attaylost of string
        val categoriesArray= arrayOfNulls<String>(categoryTitleArrayList.size)
        for (i in categoryTitleArrayList.indices){
            categoriesArray[i]=categoryTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog,position->
                //handle clilck, save clicked category id and title
                selectedCategoryId=categoryIdArrayList[position]
                selectedCategoryTitle=categoryTitleArrayList[position]

                //set to textview
                binding.categoryTv.text=selectedCategoryTitle
            }
            .show()
    }

    private fun loadCategories() {
        Log.d(TAG,"LOAD CATEGORİS:Loading categories....")

        categoryTitleArrayList= ArrayList()
        categoryIdArrayList= ArrayList()


        val ref=FirebaseDatabase.getInstance().getReference("Category")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data inro them
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()
                for (ds in snapshot.children){
                    val id= "${ds.child("id").value}"
                    val category= "${ds.child("category").value}"
                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG,"onDataChange: Category ID $id")
                    Log.d(TAG,"onDataChange: Category $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}