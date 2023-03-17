package com.hera.bookapp

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import org.w3c.dom.Text
import java.sql.Timestamp
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            //format dd/mm/yy
            return android.text.format.DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        //funcion to get pdf siz
        fun loadPdfSiz(pdfUrl: String, pdfTile: String, sizeTv: TextView) {
            val TAG = "PDF_SIZE_TAG"
            //using url we can get file and medata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetaData ->
                    Log.d(TAG, "LoadPdfSize: got metadata")
                    val bytes = storageMetaData.sizeBytes.toDouble()
                    Log.d(TAG, "LoadPdfSize:Size bytes $bytes")

                    //convert bytes to KB/MB

                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb > 1) {
                        sizeTv.text = "${String.format("%.2f", mb)}MB"
                    } else if (kb >= 1) {
                        sizeTv.text = "${String.format("%.2f", kb)}KB"
                    } else {
                        sizeTv.text = "${String.format("%.2f", mb)}bytes"
                    }
                }
                .addOnFailureListener { e ->
                    //failed to get metadata
                    Log.d(TAG, "LoadPdfSize: Failed to get metada due to${e.message}")
                }
        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTile: String,
            pdfView: PDFView,
            progresbar: ProgressBar,
            pagesTv: TextView?
        ) {
            //using url we
            val TAG = "PDF_THUMBNNAOL_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.Max_Bytes_pdf)
                .addOnSuccessListener { bytes ->
                    Log.d(TAG, "LoadPdfSize: got metadata")
                    Log.d(TAG, "LoadPdfSize:Size bytes $bytes")

                    //sET TO PDFVİEW
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError { t ->
                            progresbar.visibility = View.INVISIBLE
                            Log.d(TAG, "LoadPdfFromUrlSinglePage:  ${t.message}")
                        }
                        .onPageError { page, t ->
                            progresbar.visibility = View.INVISIBLE
                            Log.d(TAG, "LoadPdfFromUrlSinglePage:  ${t.message}")
                        }
                        .onLoad { nbPages ->
                            Log.d(TAG,"LoadpdfromUrlSinglePage:Page: $nbPages")
                            //pdf loaded, we can set page count,pdf thumbnail
                            progresbar.visibility = View.INVISIBLE

                            //if pagesTv param is not null them set page numbers
                            if (pagesTv != null) {
                                pagesTv.text = "$nbPages"
                            }
                        }
                        .load()
                }
                .addOnFailureListener { e ->
                    //failed to get metadata
                    Log.d(TAG, "LoadPdfSize: Failed to get metada due to${e.message}")
                }
        }

        fun loadCategory(categoryId: String, categoryTv: TextView) {
            //load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Category")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get category
                        val category: String = "${snapshot.child("category").value}"

                        //set category
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        fun deletebook(context: Context, bookId:String,bookurl:String,bookTitle:String){
            //param details
            //1)context, used when require e.g for progressdialog toas
            //2)bookıd, to delete book from db
            //3)bookurl,delte book from firebase storage
            //3)booktitle, show in dialog etc

            val TAG="DELETE_BOOK_TAG"
            Log.d(TAG,"deletebook: deleteing...")

            //progress dialog
            val progressDialog=ProgressDialog(context)
            progressDialog.setTitle( "Please wait")
            progressDialog.setMessage("deleting $bookTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG,"DELETEBOOK: dELTİNG FROM STROAGE....")
            val storage=FirebaseStorage.getInstance().getReferenceFromUrl(bookurl)
            storage.delete()
                .addOnSuccessListener {
                    Log.d(TAG,"deletebook: deleted from storage")
                    Log.d(TAG,"DELETEBOOK:DELETİNG FROM DB NOW...")

                    val ref=FirebaseDatabase.getInstance().getReference("Books")
                    ref.child(bookId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context,"sucessfully",Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {e->
                            Log.d(TAG,"deletebook: Failed to from firebase db ${e.message}")

                            Toast.makeText(context,"Failed to delete from db due to${e.message}",Toast.LENGTH_SHORT).show()
                        }

                }
                .addOnFailureListener {e->
                    //failed
                    Log.d(TAG,"deletebook: Failed to from firebase ${e.message}")

                    Toast.makeText(context,"Failed to delete from strage due to${e.message}",Toast.LENGTH_SHORT).show()

                }

        }

        fun incrementBookViewCount(bookId:String){
            //get current book views count

            val ref=FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                       //get views count
                        var viewsCount =""+ snapshot.child("viewCount").value
                        if (viewsCount==""|| viewsCount=="null"){
                            viewsCount = "0";
                        }
                        //2Increment views count
                        val newViewsCount = viewsCount.toLong() + 1

                        //setup data to update in db

                        val hashmap=HashMap<String,Any>()
                        hashmap["viewsCount"] = newViewsCount

                        //set to db

                        val dbref=FirebaseDatabase.getInstance().getReference("Books")
                        dbref.child(bookId)
                            .updateChildren(hashmap)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }


}