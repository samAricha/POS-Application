package com.niyaj.model

data class AddressWiseOrder(
    val cartOrderId: String,
    val orderId: String,
    val customerPhone: String,
    val totalPrice: String,
    val updatedAt: String,
    val customerName: String? = null,
)
