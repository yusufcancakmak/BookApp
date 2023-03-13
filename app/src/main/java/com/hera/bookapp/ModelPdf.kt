package com.hera.bookapp

class ModelPdf {
    var uid:String=""
    var id:String=""
    var title:String=""
    var description:String=""
    var categoryId:String=""
    var url:String=""
    var timestamp:Long=0
    var viewCount:Long=0
    var dowloadsCount:Long=0

    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        viewCount: Long,
        dowloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.viewCount = viewCount
        this.dowloadsCount = dowloadsCount
    }


}