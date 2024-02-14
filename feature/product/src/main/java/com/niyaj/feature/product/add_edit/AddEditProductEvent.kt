package com.niyaj.feature.product.add_edit

import com.niyaj.model.Category

sealed interface AddEditProductEvent {

    data class CategoryChanged(val category: Category): AddEditProductEvent

    data class ProductNameChanged(val productName: String): AddEditProductEvent

    data class ProductPriceChanged(val productPrice: String): AddEditProductEvent

    data object ProductAvailabilityChanged: AddEditProductEvent

    data class AddOrUpdateProduct(val productId: String = ""): AddEditProductEvent
}