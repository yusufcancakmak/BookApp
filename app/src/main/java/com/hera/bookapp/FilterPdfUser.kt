package com.hera.bookapp

import android.widget.Filter

class FilterPdfUser:Filter {

    val filterList:ArrayList<ModelPdf>
    var adapterPdfUser:AdapterPdfUser

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) : super() {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence?=constraint
        val results=FilterResults()
            //value to be searched should not be null and empty
        if (constraint != null&&constraint.isNotEmpty()){
            //not null nor empty
            //change to upper case, or lower case to remove case sentity
            constraint =constraint.toString().uppercase()
            val filterModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){
                if (filterList[i].title.uppercase().contains(constraint)){
                    //searched value matched with title, add to list
                    filterModels.add(filterList[i])
                }
            }
            //return filtered list and size
            results.count=filterModels.size
            results.values=filterModels

        }else{
            //either it is null or is empty

            results.count=filterList.size
            results.values=filterList


        }
        return  results

    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterPdfUser.modellist=results.values as ArrayList<ModelPdf>

        //notifcation adapter
        adapterPdfUser.notifyDataSetChanged()
    }
}