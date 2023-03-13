package com.hera.bookapp

import android.widget.Filter

/*used o filter data from rv | search pdf from list in rv*/
class FilterPdfAdmin(var filterlist:ArrayList<ModelPdf>,
                     var adapterPdfAdmin:AdapterPdfAdmin): Filter() {
    //arraylist in wich we want to search
    override fun performFiltering(p0: CharSequence?): FilterResults {
    var constraint:CharSequence?=p0
        var result= FilterResults()
        //value to be searched should not be null and not empty
        if (constraint!=null&&constraint.isNotEmpty()){
            //change to uper case, or lowercse to avid case senitivity
           constraint=constraint.toString().lowercase()
            val filterModels=ArrayList<ModelPdf>()
            for (i in filterModels.indices){
                //validate if match
                if (filterlist[i].title.lowercase().contains(constraint)){
                    //searched value is similar to value in list , add o filtered list
                    filterModels.add(filterlist[i])
                }
            }
            result.count=filterModels.size
            result.values=filterModels
        }
        else{
            //searched value is either null or empty,return all data
            result.count=filterlist.size
            result.values=filterlist
        }
        return  result//dont miss
    }

    override fun publishResults(constraint: CharSequence?, result: FilterResults) {
        //apply filter changes
        adapterPdfAdmin.pdfarraylist=result.values as ArrayList<ModelPdf>

        adapterPdfAdmin.notifyDataSetChanged()
    }


}