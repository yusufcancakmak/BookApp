package com.hera.bookapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.ActivityPdfListAdminBinding

class PdfListAdminActivity : AppCompatActivity() {
private lateinit var binding: ActivityPdfListAdminBinding
private companion object{
    const val TAG="PDF_LİST_ADMİN_TAG"
}
    //category id, title
    private var categoryId =""
    private var category=""
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
   private lateinit var adapterPdfAdmin: AdapterPdfAdmin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent =intent
        categoryId=intent.getStringExtra("categoryId")!!
        category=intent.getStringExtra("category")!!

        //set pdf category
        binding.subTitleTv.text=category

        //load pdf/books
        loadPdfList()
        //search
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //filter data

                try {
                    adapterPdfAdmin.filter!!.filter(s)
                }catch (e:Exception){
                    Log.d(TAG,"ONtEXTcHANGED:${e.message}")

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadPdfList() {
        //init arraylist
        pdfArrayList= ArrayList()

        val ref=FirebaseDatabase.getInstance().getReference("Books")
            ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                        pdfArrayList.clear()
                        for (ds in snapshot.children){
                            val model=ds.getValue(ModelPdf::class.java)
                            //add to list
                            if (model!=null){
                                pdfArrayList.add(model)
                                Log.d(TAG,"onDataChange:${model.title}${model.categoryId} ")
                            }


                        }
                        adapterPdfAdmin= AdapterPdfAdmin(pdfArrayList)
                        binding.bookRv.adapter=adapterPdfAdmin
                    }
                    //setup adapter


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

    }
}