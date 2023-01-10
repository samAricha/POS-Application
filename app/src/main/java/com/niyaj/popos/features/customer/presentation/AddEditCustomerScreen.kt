package com.niyaj.popos.features.customer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun AddEditCustomerScreen(
    customerId: String? = "",
    navController: NavController = rememberNavController(),
    customerViewModel: CustomerViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    LaunchedEffect(key1 = true) {
        customerViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!customerId.isNullOrEmpty())
            stringResource(id = R.string.edit_customer)
        else
            stringResource(id = R.string.create_customer),
        icon = Icons.Default.PersonAdd,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerPhone,
                hint = "Customer Phone",
                leadingIcon = Icons.Default.PhoneAndroid,
                error = customerViewModel.addEditCustomerState.customerPhoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerPhoneChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerName ?: "",
                hint = "Customer Name",
                leadingIcon = Icons.Default.Badge,
                error = customerViewModel.addEditCustomerState.customerNameError,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = customerViewModel.addEditCustomerState.customerEmail ?: "",
                hint = "Customer Email",
                leadingIcon = Icons.Default.Mail,
                error = customerViewModel.addEditCustomerState.customerEmailError,
                onValueChange = {
                    customerViewModel.onCustomerEvent(CustomerEvent.CustomerEmailChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButton(
                text = if (!customerId.isNullOrEmpty()) stringResource(id = R.string.edit_customer)
                else stringResource(id = R.string.create_customer),
                icon = if (!customerId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    if (!customerId.isNullOrEmpty()) {
                        customerViewModel.onCustomerEvent(CustomerEvent.UpdateCustomer(customerId))
                    } else {
                        customerViewModel.onCustomerEvent(CustomerEvent.CreateNewCustomer)
                    }
                },
            )
        }
    }
}