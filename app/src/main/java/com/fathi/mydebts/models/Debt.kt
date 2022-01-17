package com.fathi.mydebts.models

class Debt {

    var id: Int = 0
    var value: Int = 0
    var description: String = ""
    var image: ByteArray? = null
    var date: String? = ""
    var customerId: Int = 0

    constructor(value: Int, date: String, description: String, image: ByteArray?, customerId: Int) {
        this.value = value
        this.date = date
        this.description = description
        this.image = image
        this.customerId = customerId
    }

    constructor(id: Int, value: Int, date: String, description: String, customerId: Int) {
        this.id = id
        this.value = value
        this.date = date
        this.description = description
        this.image = image
        this.customerId = customerId
    }

    constructor(id: Int, value: Int, date: String, description: String, image: ByteArray, customerId: Int) {
        this.id = id
        this.value = value
        this.date = date
        this.description = description
        this.image = image
        this.customerId = customerId
    }

    constructor(value: Int, date: String, description: String, customerId: Int) {
        this.value = value
        this.date = date
        this.description = description
        this.customerId = customerId
    }

    constructor(id: Int, value: Int, date: String, customerId: Int) {
        this.id = id
        this.value = value
        this.date = date
        this.customerId = customerId
    }
}