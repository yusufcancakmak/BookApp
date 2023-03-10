package com.hera.bookapp

import android.app.AlertDialog
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.hera.bookapp.databinding.RowCategoriesBinding

class AdapterCategory(public  var categoryArrayList: ArrayList<ModelCategory>): RecyclerView.Adapter<AdapterCategory.Myviewholder>(),Filterable {

    inner class Myviewholder(val binding: RowCategoriesBinding):RecyclerView.ViewHolder(binding.root)

    private lateinit var filterList:ArrayList<ModelCategory>
    private var filter:FilterCategory? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder {
        return Myviewholder(RowCategoriesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }



    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val currentlist=categoryArrayList[position]
        val id =currentlist.id
        val uid=currentlist.uid
        val timestamp=currentlist.timestamp
        holder.binding.categoryTv.text=currentlist.category.toString()
        holder.binding.deleteBtn.setOnClickListener {
            val builder =AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Delete")
                .setMessage("Are sure you want to delete this category?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(holder.itemView.context,"Deleting",Toast.LENGTH_SHORT).show()
                    deleteCategory(currentlist, holder)

                }
                .setNegativeButton("cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }
    }

    private fun deleteCategory(model:ModelCategory,holder: Myviewholder) {
        //get id of category to delete
        val id = model.id

        val ref =FirebaseDatabase.getInstance().getReference("Category")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(holder.itemView.context,"Deleted..",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(holder.itemView.context,"Unable to delete due to${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    override fun getItemCount()=categoryArrayList.size
    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterCategory(filterList,this)
        }
        return filter as FilterCategory
    }
}