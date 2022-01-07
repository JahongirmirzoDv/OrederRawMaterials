package com.example.orderrawmaterials.models

class Data {
    var name: String? = null
    var type: String? = null
    var count: String? = null
    var date:String? = null
    var isCompleted: Boolean = false
    var summ: String? = null
    var sum_type: String? = null

    constructor()
    constructor(name: String?, type: String?, count: String?,date: String, isCompleted: Boolean) {
        this.name = name
        this.type = type
        this.count = count
        this.date = date
        this.isCompleted = isCompleted
    }

    constructor(summ: String?, sum_type: String?,isCompleted: Boolean) {
        this.summ = summ
        this.sum_type = sum_type
        this.isCompleted = isCompleted
    }

    constructor(
        name: String?,
        type: String?,
        count: String?,
        isCompleted: Boolean,
        summ: String?,
        sum_type: String?
    ) {
        this.name = name
        this.type = type
        this.count = count
        this.isCompleted = isCompleted
        this.summ = summ
        this.sum_type = sum_type
    }


}