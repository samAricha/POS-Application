package com.niyaj.database.model

import com.niyaj.model.Reports
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ReportsEntity(): RealmObject {

    @PrimaryKey
    var reportId: String = ""

    var expensesQty: Long = 0
    var expensesAmount: Long = 0

    var dineInSalesQty: Long = 0
    var dineInSalesAmount: Long = 0

    var dineOutSalesQty: Long = 0
    var dineOutSalesAmount: Long = 0

    var reportDate: String = ""

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null


    constructor(
        reportId: String = "",
        expensesQty: Long = 0,
        expensesAmount: Long = 0,
        dineInSalesQty: Long = 0,
        dineInSalesAmount: Long = 0,
        dineOutSalesQty: Long = 0,
        dineOutSalesAmount: Long = 0,
        reportDate: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.reportId = reportId
        this.expensesQty = expensesQty
        this.expensesAmount = expensesAmount
        this.dineInSalesQty = dineInSalesQty
        this.dineInSalesAmount = dineInSalesAmount
        this.dineOutSalesQty = dineOutSalesQty
        this.dineOutSalesAmount = dineOutSalesAmount
        this.reportDate = reportDate
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun ReportsEntity.toExternalModel(): Reports {
    return Reports(
        reportId = reportId,
        expensesQty = expensesQty,
        expensesAmount = expensesAmount,
        dineInSalesQty = dineInSalesQty,
        dineInSalesAmount = dineInSalesAmount,
        dineOutSalesQty = dineOutSalesQty,
        dineOutSalesAmount = dineOutSalesAmount,
        reportDate = reportDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}