package com.hera.bookapp

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hera.bookapp.databinding.RowPdfAdminBinding

class AdapterPdfAdmin(public var pdfarraylist:ArrayList<ModelPdf>):RecyclerView.Adapter<AdapterPdfAdmin.MyViewHolder>(),Filterable{

    inner class MyViewHolder(val binding: RowPdfAdminBinding):RecyclerView.ViewHolder(binding.root)
    //filter object
    private var filter:FilterPdfAdmin?=null
    private lateinit var filterlist:ArrayList<ModelPdf>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RowPdfAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentlist=pdfarraylist[position]
        val formattedDate=MyApplication.formatTimeStamp(currentlist.timestamp)
        //set data
       holder.binding.categoryTv.text=currentlist.categoryId
        holder.binding.titleTv.text=currentlist.title
        holder.binding.descriptiontv.text=currentlist.description
        holder.binding.dateTv.text=formattedDate

        //load furter details like category,pdf from url, pdf size
        //category id
        MyApplication.loadCategory(currentlist.categoryId,holder.binding.categoryTv)

        //we dont need page number here, pass null type page number

        MyApplication.loadPdfFromUrlSinglePage(currentlist.url,currentlist.title,holder.binding.pdfview,holder.binding.prgreesbar,null)

        //load pdf size
        MyApplication.loadPdfSiz(currentlist.url,currentlist.title,holder.binding.sizeTv)

        //handle click, show dialog with options 1) edit book, 2) delete book
        holder.binding.moreBTN.setOnClickListener {
            moreOptionsDialog(currentlist, holder)
        }

        //handle itemclick /pdfdetailsActivity activity , lets create it first
        holder.itemView.setOnClickListener {
        val intent=Intent(holder.itemView.context,PdfDetailsActivity::class.java)
            intent.putExtra("bookId",currentlist.id) //wiiil be used load pdf details
            holder.itemView.context.startActivity(intent)

        }

    }

    private fun moreOptionsDialog(currentlist: ModelPdf, holder: AdapterPdfAdmin.MyViewHolder) {
        //get id, url title of book
        val bookId = currentlist.id
        val bookUrl =currentlist.url
        val bookTtitle= currentlist.title
        //options to show in dialog
        val options= arrayOf("Edit","Delete")

        //alert dialog
        val builder =AlertDialog.Builder(holder.itemView.context)
        builder.setItems(options){dialog, position->
            //handle item click
            if (position==0){
                //edit is clik
                val intent=Intent(holder.itemView.context,PdfEditActivity::class.java)
                intent.putExtra("bookId",bookId)//passed bookıD, will be used edit he book
                holder.itemView.context.startActivity(intent)
            }
            else if (position==1){
                //delete is click, create fun in myapplicaton

                //show confirm dialog firest if you need...
                MyApplication.deletebook(holder.itemView.context,bookId,bookUrl,bookTtitle)
            }

        }
            .show()
    }


    override fun getItemCount()=pdfarraylist.size
    override fun getFilter(): Filter {
        if (filter==null){
            filter= FilterPdfAdmin(filterlist,this)
        }
        return filter as FilterPdfAdmin
    }


}