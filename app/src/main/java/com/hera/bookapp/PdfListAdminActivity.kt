package com.hera.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hera.bookapp.databinding.ActivityPdfListAdminBinding

class PdfListAdminActivity : AppCompatActivity() {
private lateinit var binding: ActivityPdfListAdminBinding
    //category id, title
    private var categoryId =""
    private var category=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent =intent
        categoryId=intent.getStringExtra("categoryId")!!
        category=intent.getStringExtra("category")!!
    }
}