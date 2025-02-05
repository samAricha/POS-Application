package com.niyaj.feature.order.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.OrderTestTags
import com.niyaj.common.tags.OrderTestTags.ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.OrderTestTags.SEARCH_ORDER_NOT_AVAILABLE
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.DineInOrder
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.event.UiState

@Composable
fun DineInOrderedItemLayout(
    dineInState: UiState<List<DineInOrder>>,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onMarkedAsProcessing: (String) -> Unit,
    onClickViewDetails: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    onClickAddProduct: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Crossfade(
            targetState = dineInState,
            label = "DineOut Orders::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (showSearchBar) SEARCH_ORDER_NOT_AVAILABLE else ORDER_NOT_AVAILABLE,
                        buttonText = OrderTestTags.ADD_ITEM_TO_CART,
                        image = R.drawable.emptycarttwo,
                        onClick = onClickAddProduct
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        state = lazyListState
                    ) {
                        items(
                            items = state.data,
                            key = { it.cartOrderId }
                        ) { dineInOrder ->
                            OrderedItem(
                                orderId = dineInOrder.orderId,
                                orderPrice = dineInOrder.totalAmount,
                                orderDate = dineInOrder.updatedAt,
                                customerPhone = null,
                                customerAddress = null,
                                onClickPrintOrder = {
                                    onClickPrintOrder(dineInOrder.cartOrderId)
                                },
                                onMarkedAsProcessing = {
                                    onMarkedAsProcessing(dineInOrder.cartOrderId)
                                },
                                onClickDelete = {
                                    onClickDelete(dineInOrder.cartOrderId)
                                },
                                onClickViewDetails = {
                                    onClickViewDetails(dineInOrder.cartOrderId)
                                },
                                onClickEdit = {
                                    onClickEdit(dineInOrder.cartOrderId)
                                },
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}