package com.niyaj.database.model

import com.niyaj.model.Customer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CustomerEntity(): RealmObject {

    @PrimaryKey
    var customerId: String = ""

    var customerPhone: String = ""

    var customerName: String? = null

    var customerEmail: String? = null

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        customerId: String = "",
        customerPhone: String = "",
        customerName: String? = null,
        customerEmail: String? = null,
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ): this() {
        this.customerId = customerId
        this.customerPhone = customerPhone
        this.customerName = customerName
        this.customerEmail = customerEmail
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}


fun CustomerEntity.toExternalModel(): Customer {
    return Customer(
        customerId = customerId,
        customerPhone = customerPhone,
        customerName = customerName,
        customerEmail = customerEmail,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}