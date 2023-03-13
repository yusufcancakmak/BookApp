package com.hera.bookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hera.bookapp.databinding.RowPdfAdminBinding

class AdapterPdfAdmin(public var pdfarraylist:ArrayList<ModelPdf>):RecyclerView.Adapter<AdapterPdfAdmin.MyViewHolder>(),Filterable{

    inner class MyViewHolder(val binding: RowPdfAdminBinding):RecyclerView.ViewHolder(binding.root)
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

    }


    override fun getItemCount()=pdfarraylist.size
    override fun getFilter(): Filter {
        if (filter==null){
            filter= FilterPdfAdmin(filterlist,this)
        }
        return filter as FilterPdfAdmin
    }


}