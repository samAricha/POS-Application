package com.niyaj.popos.features.order.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.cart.presentation.CartTabItem
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.util.Tabs
import com.niyaj.popos.features.components.util.TabsContent
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.niyaj.popos.features.order.presentation.print_order.PrintViewModel
import com.niyaj.popos.util.toFormattedDate
import com.niyaj.popos.util.toFormattedTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import de.charlex.compose.RevealSwipe
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Destination
@Composable
fun OrderScreen(
    selectedDate: String = "",
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    orderViewModel: OrderViewModel = hiltViewModel(),
    printViewModel: PrintViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState()
    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    val showSearchBar by orderViewModel.toggledSearchBar.collectAsStateWithLifecycle()

    val orders = orderViewModel.orders.collectAsStateWithLifecycle().value.orders

    val dineInOrders by remember(orders) {
        derivedStateOf {
            orders.filter { cart ->
                cart.cartOrder?.orderType == CartOrderType.DineIn.orderType
            }
        }
    }

    val dineOutOrders by remember(orders) {
        derivedStateOf {
            orders.filter { cart ->
                cart.cartOrder?.orderType == CartOrderType.DineOut.orderType
            }
        }
    }

    val isLoading: Boolean = orderViewModel.orders.collectAsStateWithLifecycle().value.isLoading
    val error = orderViewModel.orders.collectAsStateWithLifecycle().value.error

    val selectedDate1 = orderViewModel.selectedDate.collectAsStateWithLifecycle().value

    var deletableOrder by remember { mutableStateOf("") }

    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true){
        orderViewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.OnSuccess -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        navController.navigate(CartScreenDestination())
                    }
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            orderViewModel.onSearchBarCloseAndClearClick()
        } else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        onBackButtonClick = {
            if (showSearchBar){
                orderViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        navigationIcon = {},
        title = {
            Text(text = "Orders")
        },
        navActions = {
            if(showSearchBar){
                StandardSearchBar(
                    searchText = orderViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for orders...",
                    onSearchTextChanged = {
                        orderViewModel.onOrderEvent(OrderEvent.OnSearchOrder(it))
                    },
                    onClearClick = {
                        orderViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (selectedDate1.isNotEmpty() && selectedDate1 != LocalDate.now().toString()) {
                    RoundedBox(
                        text = selectedDate1.toFormattedDate,
                        onClick = {
                            dialogState.show()
                        }
                    )
                    Spacer(modifier = Modifier.width(SpaceMini))
                }else{
                    IconButton(
                        onClick = { dialogState.show() }
                    ) {
                        Icon(imageVector = Icons.Default.Today, contentDescription = "Choose Date")
                    }
                }

                if (orders.isNotEmpty()){
                    IconButton(
                        onClick = {
                            orderViewModel.onOrderEvent(OrderEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                    IconButton(
                        onClick = {
                            onOpenSheet(
                                BottomSheetScreen.FilterOrderScreen(
                                    filterOrder = orderViewModel.orders.value.filterOrder,
                                    onFilterChanged = {
                                        orderViewModel.onOrderEvent(OrderEvent.OnFilterOrder(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_order),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }

                    if(pagerState.currentPage == 1){
                        IconButton(
                            onClick = {
                                showMenu = !showMenu
                            }
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "View More")
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu  = false },
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    orderViewModel.onOrderEvent(OrderEvent.PrintDeliveryReport)
                                    showMenu = false
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = "Print Delivery Reports",
                                        tint = MaterialTheme.colors.secondaryVariant
                                    )
                                    Spacer(modifier = Modifier.width(SpaceSmall))
                                    Text(
                                        text = "Print Delivery Reports",
                                        style = MaterialTheme.typography.body1,
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    ){
        MaterialDialog(
            dialogState = deleteOrderState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        orderViewModel.onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
                        deletableOrder = ""
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        deleteOrderState.hide()
                        deletableOrder = ""
                    },
                )
            }
        ) {
            title(text = "Delete Order?")
            message(res = R.string.delete_order_msg)
        }

        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("Ok")
                negativeButton("Cancel")
            }
        ) {
            datepicker { date ->
                orderViewModel.onOrderEvent(OrderEvent.SelectDate(date.toString()))
            }
        }

        val tabs = listOf(
            CartTabItem.DineInItem {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if(dineInOrders.isEmpty() || error != null){
                        ItemNotAvailable(
                            text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_order),
                            buttonText = stringResource(id = R.string.add_items_to_cart_button),
                            onClick = {
                                navController.navigate(MainFeedScreenDestination())
                            }
                        )
                    } else if(isLoading){
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                        ){
                            items(dineInOrders){ order ->
                                if(order.cartOrder != null && order.cartProducts.isNotEmpty()) {
                                    RevealSwipe(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        onContentClick = {},
                                        maxRevealDp = 150.dp,
                                        hiddenContentStart = {
                                            IconButton(onClick = {
                                                orderViewModel.onOrderEvent(
                                                    OrderEvent.MarkedAsProcessing(
                                                        order.cartOrder.cartOrderId
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(SpaceSmall))

                                            IconButton(
                                                onClick = {
                                                    navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = order.cartOrder.cartOrderId))
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                                            }
                                        },
                                        hiddenContentEnd = {
                                            IconButton(onClick = {
                                                orderViewModel.onOrderEvent(
                                                    OrderEvent.MarkedAsDelivered(
                                                        order.cartOrder.cartOrderId
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Outbox,
                                                    contentDescription = "Mark as Delivered",
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }

                                            IconButton(onClick = {
                                                deleteOrderState.show()
                                                deletableOrder = order.cartOrder.cartOrderId
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete order",
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }
                                        },
                                        contentColor = MaterialTheme.colors.primary,
                                        backgroundCardContentColor = LightColor12,
                                        backgroundCardStartColor = MaterialTheme.colors.primary,
                                        backgroundCardEndColor = MaterialTheme.colors.error,
                                        shape = RoundedCornerShape(6.dp),
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shape = it,
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(SpaceSmall),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Column(
                                                        verticalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        TextWithIcon(
                                                            text = order.cartOrder.orderId,
                                                            icon = Icons.Default.Tag
                                                        )

                                                        if(order.cartOrder.customer?.customerPhone != null) {
                                                            Spacer(modifier = Modifier.height(
                                                                SpaceSmall
                                                            ))

                                                            TextWithIcon(
                                                                text = order.cartOrder.customer!!.customerPhone,
                                                                icon = Icons.Default.PhoneAndroid
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                                        order.cartOrder.updatedAt?.toFormattedTime?.let { it1 ->
                                                            TextWithIcon(
                                                                text = it1,
                                                                icon = Icons.Default.AccessTime
                                                            )
                                                        }
                                                    }

                                                    Column(
                                                        verticalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        if (order.cartOrder.address?.shortName != null) {
                                                            TextWithIcon(
                                                                text = order.cartOrder.address!!.shortName,
                                                                icon = Icons.Default.Place
                                                            )
                                                            Spacer(modifier = Modifier.height(
                                                                SpaceSmall
                                                            ))
                                                        }

                                                        TextWithIcon(
                                                            text = (order.orderPrice.first.minus(order.orderPrice.second)).toString(),
                                                            icon = ImageVector.vectorResource(id = R.drawable.round_currency_rupee_20)
                                                        )
                                                    }

                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ){
                                                        IconButton(
                                                            onClick = {
                                                                navController.navigate(OrderDetailsScreenDestination(cartOrderId = order.cartOrder.cartOrderId))
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Visibility,
                                                                contentDescription = stringResource(id = R.string.order_details),
                                                                tint = MaterialTheme.colors.primary,
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.width(SpaceMini))

                                                        IconButton(
                                                            onClick = {
                                                                printViewModel.onPrintEvent(
                                                                    PrintEvent.PrintOrder(order.cartOrder.cartOrderId))
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Print,
                                                                contentDescription = stringResource(id = R.string.print_order),
                                                                tint = MaterialTheme.colors.primary,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                }
                            }
                        }
                    }
                }
            },

            CartTabItem.DineOutItem {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if(dineOutOrders.isEmpty() || error != null){
                        ItemNotAvailable(
                            text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_order),
                            buttonText = stringResource(id = R.string.add_items_to_cart_button),
                            onClick = {
                                navController.navigate(MainFeedScreenDestination())
                            }
                        )
                    } else if(isLoading){
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                        ){
                            items(dineOutOrders){ order ->
                                if(order.cartOrder != null && order.cartProducts.isNotEmpty()) {
                                    RevealSwipe(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        onContentClick = {},
                                        maxRevealDp = 150.dp,
                                        hiddenContentStart = {
                                            IconButton(onClick = {
                                                orderViewModel.onOrderEvent(
                                                    OrderEvent.MarkedAsProcessing(
                                                        order.cartOrder.cartOrderId
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(SpaceSmall))

                                            IconButton(
                                                onClick = {
                                                    navController.navigate(AddEditCartOrderScreenDestination(cartOrderId= order.cartOrder.cartOrderId))
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                                            }
                                        },
                                        hiddenContentEnd = {
                                            IconButton(onClick = {
                                                orderViewModel.onOrderEvent(
                                                    OrderEvent.MarkedAsDelivered(
                                                        order.cartOrder.cartOrderId
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Outbox,
                                                    contentDescription = "Mark as Delivered",
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }

                                            IconButton(onClick = {
                                                deleteOrderState.show()
                                                deletableOrder = order.cartOrder.cartOrderId
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete order",
                                                    modifier = Modifier.padding(horizontal = 25.dp),
                                                )
                                            }
                                        },
                                        contentColor = MaterialTheme.colors.primary,
                                        backgroundCardContentColor = LightColor12,
                                        backgroundCardStartColor = MaterialTheme.colors.primary,
                                        backgroundCardEndColor = MaterialTheme.colors.error,
                                        shape = RoundedCornerShape(6.dp),
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shape = it,
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(SpaceSmall),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Column(
                                                        verticalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        TextWithIcon(
                                                            text = order.cartOrder.orderId,
                                                            icon = Icons.Default.Tag
                                                        )

                                                        if(order.cartOrder.customer?.customerPhone != null) {
                                                            Spacer(modifier = Modifier.height(
                                                                SpaceSmall
                                                            ))

                                                            TextWithIcon(
                                                                text = order.cartOrder.customer!!.customerPhone,
                                                                icon = Icons.Default.PhoneAndroid
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                                        order.cartOrder.updatedAt?.toFormattedTime?.let { it1 ->
                                                            TextWithIcon(
                                                                text = it1,
                                                                icon = Icons.Default.AccessTime
                                                            )
                                                        }
                                                    }

                                                    Column(
                                                        verticalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        if (order.cartOrder.address?.shortName != null) {
                                                            TextWithIcon(
                                                                text = order.cartOrder.address!!.shortName,
                                                                icon = Icons.Default.Place
                                                            )
                                                            Spacer(modifier = Modifier.height(
                                                                SpaceSmall
                                                            ))
                                                        }

                                                        TextWithIcon(
                                                            text = (order.orderPrice.first.minus(order.orderPrice.second)).toString(),
                                                            icon = ImageVector.vectorResource(id = R.drawable.round_currency_rupee_20)
                                                        )
                                                    }

                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ){
                                                        IconButton(
                                                            onClick = {
                                                                navController.navigate(OrderDetailsScreenDestination(cartOrderId = order.cartOrder.cartOrderId))
                                                            }
                                                        ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Visibility,
                                                                contentDescription = stringResource(id = R.string.order_details),
                                                                tint = MaterialTheme.colors.primary,
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.width(SpaceMini))

                                                        IconButton(
                                                            onClick = {
                                                                printViewModel.onPrintEvent(
                                                                    PrintEvent.PrintOrder(order.cartOrder.cartOrderId))
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Print,
                                                                contentDescription = stringResource(id = R.string.print_order),
                                                                tint = MaterialTheme.colors.primary,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                }
                            }
                        }
                    }
                }
            }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                orderViewModel.onOrderEvent(OrderEvent.RefreshOrder)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Tabs(tabs = tabs, pagerState = pagerState)
                TabsContent(tabs = tabs, pagerState = pagerState)
            }
        }
    }
}