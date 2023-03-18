package com.hera.bookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            uid=args.getString(uid)!!
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentBookUserBinding.inflate(LayoutInflater.from(context),container,false)
        return binding.root
    }


    }
