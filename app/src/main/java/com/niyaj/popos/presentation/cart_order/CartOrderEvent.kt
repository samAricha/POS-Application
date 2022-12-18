package com.niyaj.popos.presentation.cart_order

sealed class CartOrderEvent{

    data class DeleteCartOrder(val cartOrderId: String): CartOrderEvent()

    data class SelectCartOrderEvent(val cartOrderId: String) : CartOrderEvent()

    data class SelectCartOrder(val cartOrderId: String) : CartOrderEvent()

    data class OnSearchCartOrder(val searchText: String): CartOrderEvent()

    object DeletePastSevenDaysBeforeData: CartOrderEvent()

    object DeleteAllCartOrders: CartOrderEvent()

    object ToggleSearchBar : CartOrderEvent()

    object RefreshCartOrder: CartOrderEvent()
}