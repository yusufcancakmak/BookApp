package com.hera.bookapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hera.bookapp.databinding.FragmentBookUserBinding


class BookUserFragment() : Fragment() {
private lateinit var binding: FragmentBookUserBinding

public companion object{
    private const val TAG="BOOKS_USER_TAG"

    //Receive data from activity to load books e.g categoryId, category, uid
    public fun newInstance(categoryId:String,category:String, uid:String):BookUserFragment{
        val fragment =BookUserFragment()

        //put data bundle intent
        val args=Bundle()
        args.putString("categoryId",categoryId)
        args.putString("category",category)
        args.putString("uid",uid)
        fragment.arguments =args
        return fragment
    }
}
    private var categoryId=""
    private var category=""
    private var uid =""
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get arguments
        val args=arguments
        if (args!=null){
            categoryId=args.getString("categoryId")!!
            category=args.getString("category")!!
            uid=args.getString("uid")!!
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentBookUserBinding.inflate(LayoutInflater.from(context),container,false)
        //load pdf according to category, this fragment will have new instance to load each category pdfs
        Log.d(TAG,"onCreateView:Category:$category")
        if (category=="All"){
            //load all books
            loadAllBooks()
        }
        else if(category=="Most Viewed"){

            //load most viewed books
            loadMostViewedDownloadedBooks("viewsCount")

        }
        else if (category=="Most Downloaded"){
            loadMostViewedDownloadedBooks("downloadsCount")

        }
        else{
            //load selected category books
            loadCategoryizedBooks()
        }
        //search
        binding.searchEt.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    try {
                        adapterPdfUser.filter.filter(s)

                    }catch (e:Exception){
                        Log.d(TAG,"onTextChange:SEARCH EXCEP. :${e.message}")
                    }

                }

                override fun afterTextChanged(p0: Editable?) {

                }


             }}



        return binding.root
    }

    private fun loadAllBooks() {
       pdfArrayList= ArrayList()
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model =ds.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(pdfArrayList, filterList = pdfArrayList)
                //set adapter to rv
                binding.bookRv.adapter=adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        pdfArrayList= ArrayList()
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy).limitToLast(10)//load most viewed or most mownloaded books. order
            .addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model =ds.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser =AdapterPdfUser(pdfArrayList,pdfArrayList)
                //set adapter to rv
                binding.bookRv.adapter=adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun loadCategoryizedBooks() {
        pdfArrayList= ArrayList()
        val ref=FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        val model =ds.getValue(ModelPdf::class.java)
                        //add to list
                        pdfArrayList.add(model!!)
                    }
                    //setup adapter
                    adapterPdfUser =AdapterPdfUser(pdfArrayList,pdfArrayList)
                    //set adapter to rv
                    binding.bookRv.adapter=adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}
