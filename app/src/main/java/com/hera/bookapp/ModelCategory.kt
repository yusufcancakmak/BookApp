package com.hera.bookapp

class ModelCategory {
    var id:String=""
    var category:String=""
    var timestamp :Long = 0
    var uid:String =""
    // empty const. firebase required
    constructor()
    //parametize const.
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }


}