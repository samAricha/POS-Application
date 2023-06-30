package com.niyaj.popos.features.customer.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.features.common.ui.theme.LightColor21
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.model.CustomerWiseOrder
import com.niyaj.popos.features.destinations.AddEditCustomerScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags
import com.niyaj.popos.utils.isSameDay
import com.niyaj.popos.utils.toBarDate
import com.niyaj.popos.utils.toDate
import com.niyaj.popos.utils.toFormattedDateAndTime
import com.niyaj.popos.utils.toPrettyDate
import com.niyaj.popos.utils.toRupee
import com.niyaj.popos.utils.toTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import io.sentry.compose.SentryTraced

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun CustomerDetailsScreen(
    customerId : String,
    scaffoldState : ScaffoldState = rememberScaffoldState(),
    navController : NavController = rememberNavController(),
    viewModel : CustomerDetailsViewModel = hiltViewModel(),
) {

    val lazyListState = rememberLazyListState()

    val customer = viewModel.customerDetails.collectAsStateWithLifecycle().value

    val orderDetails = viewModel.orderDetails.collectAsStateWithLifecycle().value.orderDetails
    val groupedByDate = orderDetails.groupBy { it.updatedAt.toPrettyDate() }
    val isLoading = viewModel.orderDetails.collectAsStateWithLifecycle().value.isLoading
    val error = viewModel.orderDetails.collectAsStateWithLifecycle().value.error

    val totalOrders = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var detailsExpanded by remember {
        mutableStateOf(true)
    }
    var orderExpanded by remember {
        mutableStateOf(true)
    }

    SentryTraced(tag = "CustomerDetails-$customerId") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            navActions = {},
            title = {
                Text(text = "Customer Details")
            },
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ) {
                item(key = "TotalOrder Details") {
                    TotalOrderDetailsCard(details = totalOrders)
                }

                item(key = "Address Details") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    CustomerDetailsCard(
                        customer = customer,
                        onExpanded = {
                            detailsExpanded = !detailsExpanded
                        },
                        doesExpanded = detailsExpanded,
                        onClickEdit = {
                            navController.navigate(AddEditCustomerScreenDestination(customerId = it))
                        }
                    )
                }

                item(key = "OrderDetails") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    RecentOrders(
                        recentOrders = groupedByDate,
                        isLoading = isLoading,
                        error = error,
                        doesExpanded = orderExpanded,
                        onExpanded = {
                            orderExpanded = !orderExpanded
                        },
                        onClickOrder = {
                            navController.navigate(OrderDetailsScreenDestination(cartOrderId = it))
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}


@Composable
fun TotalOrderDetailsCard(
    details : TotalCustomerOrderDetails,
) {
    Card(
        modifier = Modifier
            .testTag("CalculateSalary")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Total Orders",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = details.totalAmount.toRupee,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(EmployeeTestTags.REMAINING_AMOUNT_TEXT)
                )

                val startDate = details.datePeriod.first
                val endDate = details.datePeriod.second

                if (startDate.isNotEmpty()) {
                    Card(
                        backgroundColor = LightColor21,
                        modifier = Modifier.testTag("DatePeriod")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceMini),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = startDate.toBarDate,
                                style = MaterialTheme.typography.body2,
                            )

                            if (endDate.isNotEmpty()) {
                                if (!details.datePeriod.isSameDay()) {
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Icon(imageVector = Icons.Default.ArrowRightAlt, contentDescription = "DatePeriod")
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Text(
                                        text = endDate.toBarDate,
                                        style = MaterialTheme.typography.body2,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    backgroundColor = LightColor6,
                    modifier = Modifier.testTag("TotalOrders")
                ) {
                    Text(
                        text = "Total ${details.totalOrder} Order",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(SpaceSmall)
                    )
                }

                Spacer(modifier = Modifier.width(SpaceSmall))

                Card(
                    backgroundColor = LightColor6,
                    modifier = Modifier.testTag("RepeatedOrder")
                ) {
                    Text(
                        text = "${details.repeatedOrder} Repeated Order",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(SpaceSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomerDetailsCard(
    customer: Customer,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: (String) -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Customer Details",
                    icon = Icons.Default.Business,
                    isTitle = true
                )
            },
            trailing = {
                IconButton(
                    onClick = {
                        onClickEdit(customer.customerId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall)
                ) {
                    TextWithIcon(
                        modifier = Modifier.testTag(customer.customerPhone),
                        text = "Phone - ${customer.customerPhone}",
                        icon = Icons.Default.PhoneAndroid
                    )
                    customer.customerName?.let { name ->
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        TextWithIcon(
                            modifier = Modifier.testTag(name),
                            text = "Name - $name",
                            icon = Icons.Default.Person
                        )
                    }

                    customer.customerEmail?.let { email ->
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            modifier = Modifier.testTag(email),
                            text = "Email : $email",
                            icon = Icons.Default.Email
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        modifier = Modifier.testTag(customer.createdAt.toDate),
                        text = "Created At : ${customer.createdAt.toFormattedDateAndTime}",
                        icon = Icons.Default.CalendarToday
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        TextWithIcon(
                            text = "Updated At : ${it.toFormattedDateAndTime}",
                            icon = Icons.Default.Login
                        )
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentOrders(
    recentOrders: Map<String, List<CustomerWiseOrder>>,
    isLoading: Boolean = false,
    error : String? = null,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (String) -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Recent Orders",
                    icon = Icons.Default.AllInbox,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                if (isLoading){
                    LoadingIndicator()
                }else if (recentOrders.isEmpty() || error != null) {
                    ItemNotAvailable(text = error ?: "No orders made using this customer.")
                }else {
                    Column {
                        recentOrders.forEach { (date, orders) ->
                            TextWithCount(
                                modifier = Modifier
                                    .background(Color.Transparent),
                                text = date,
                                count = orders.size,
                            )

                            orders.forEachIndexed { index, order ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onClickOrder(order.cartOrderId)
                                        }
                                        .padding(SpaceSmall),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextWithIcon(
                                        text = order.orderId,
                                        icon = Icons.Default.Tag,
                                        isTitle = true,
                                    )

                                    order.customerAddress?.let {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Text(text = it)
                                    }

                                    Text(
                                        text = order.totalPrice.toRupee,
                                        textAlign = TextAlign.Start
                                    )

                                    Text(
                                        text = order.updatedAt.toTime,
                                        textAlign = TextAlign.End
                                    )
                                }

                                if (index != orders.size - 1) {
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                    Divider(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(SpaceMini))
                                }
                            }

                            Divider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        )
    }
}