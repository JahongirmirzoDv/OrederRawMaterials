package com.example.orderrawmaterials.models

class User {
    var name: String? = null
    var last_name: String? = null
    var type: String? = null
    var uid: String? = null
    var token:String? = null


    constructor(name: String?, last_name: String?, type: String?) {
        this.name = name
        this.last_name = last_name
        this.type = type
    }


    constructor()
    constructor(name: String?, last_name: String?, type: String?, uid: String?, token: String?) {
        this.name = name
        this.last_name = last_name
        this.type = type
        this.uid = uid
        this.token = token
    }

    constructor(name: String?, last_name: String?, type: String?, uid: String?) {
        this.name = name
        this.last_name = last_name
        this.type = type
        this.uid = uid
    }

}
