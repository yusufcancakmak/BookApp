package com.hera.bookapp

import android.widget.Filter

class FilterCategory:Filter {

    private var filterlist:ArrayList<ModelCategory>
    //adapter in wich filter need to be implemented
    private var adapterCategory:AdapterCategory

    //const.

    constructor(filterlist: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterlist = filterlist
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(p0: CharSequence?): FilterResults {
        var constraint =p0
        val results = FilterResults()

        if (constraint!=null && constraint.isNotEmpty()){
            //search value is nor null not empty

            //change to upper case, oer lower case to avaid
            constraint=constraint.toString().uppercase()
            val filteredModel:ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterlist.size){
                //validate
                if (filterlist[i].category.uppercase().contains(constraint)){
                    //add to filtereed list
                    filteredModel.add(filterlist[i])
                }
            }
            results.count = filteredModel.size
            results.values = filteredModel
        }
        else{
            results.count = filterlist.size
            results.values = filterlist


        }
        return  results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
       //apply filter change

        adapterCategory.categoryArrayList = results!!.values as ArrayList<ModelCategory>

        adapterCategory.notifyDataSetChanged()
    }
}