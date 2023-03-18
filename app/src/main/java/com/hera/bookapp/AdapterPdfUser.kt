package com.hera.bookapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hera.bookapp.databinding.RowPdfUserBinding

//to access in filter class, make public
class AdapterPdfUser(
    public var modellist: ArrayList<ModelPdf>,
    public var filterList: ArrayList<ModelPdf>
) :
    RecyclerView.Adapter<AdapterPdfUser.MyViewHolder>(), Filterable {
    private  var filter:FilterPdfUser?=null

    inner class MyViewHolder(val binding: RowPdfUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            RowPdfUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentlist = modellist[position]
        val bookId = currentlist.id
        val date = MyApplication.formatTimeStamp(currentlist.timestamp)
        holder.binding.titleTv.text = currentlist.title
        holder.binding.descriptiontv.text = currentlist.description
        holder.binding.dateTv.text = date

        MyApplication.loadPdfFromUrlSinglePage(
            currentlist.url,
            currentlist.title,
            holder.binding.pdfView,
            holder.binding.progressbar,
            null
        )
        MyApplication.loadCategory(
            currentlist.categoryId,
            holder.binding.categoryTv
        )
        MyApplication.loadPdfSiz(
            currentlist.url,
            currentlist.title,
            holder.binding.sizeTv
        )

        //handle click,open pdf details
        holder.itemView.setOnClickListener {
            //pas book id in intent, will be used to get pdf info
            val intent =
                Intent(holder.itemView.context, PdfDetailsActivity::class.java)
            intent.putExtra("bookId", bookId)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = modellist.size


    override fun getFilter(): Filter {
    if (filter==null){
        filter= FilterPdfUser(filterList,this)
    }
        return filter as FilterPdfUser

    }
}