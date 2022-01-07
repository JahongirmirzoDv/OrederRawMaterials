package com.example.orderrawmaterials.notification.models

class NotifyData {
    var user: String? = null
    var icon: Int? = null
    var body: String? = null
    var title: String? = null
    var sented: String? = null

    constructor(user: String?, icon: Int?, body: String?, title: String?, sented: String?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }

    constructor()


}