package com.fathi.mydebts.models

class Customer {

    var id: Int = 0
    var name: String = ""
    var phone: String = ""

    constructor(name: String, phone: String) {
        this.name = name
        this.phone = phone
    }

    constructor(id: Int, name: String, phone: String) {
        this.id = id
        this.name = name
        this.phone = phone
    }
}
