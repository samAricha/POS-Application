package com.niyaj.popos.features.address.presentation.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DomainAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
fun AddEditAddressScreen(
    addressId: String? = "",
    navController: NavController,
    addEditAddressViewModel: AddEditAddressViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {

    LaunchedEffect(key1 = true) {
        addEditAddressViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }
    
    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = if (!addressId.isNullOrEmpty())
            stringResource(id = R.string.edit_address)
        else
            stringResource(id = R.string.create_address),
        icon = Icons.Default.DomainAdd,
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
                text = addEditAddressViewModel.addEditAddressState.address,
                hint = "Full Address",
                leadingIcon = Icons.Default.Business,
                error = addEditAddressViewModel.addEditAddressState.addressError,
                onValueChange = {
                    addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.AddressNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardOutlinedTextField(
                modifier = Modifier,
                text = addEditAddressViewModel.addEditAddressState.shortName,
                hint = "Short Name",
                leadingIcon = Icons.Default.ShortText,
                error = addEditAddressViewModel.addEditAddressState.shortNameError,
                onValueChange = {
                    addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.ShortNameChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardButton(
                text = if (!addressId.isNullOrEmpty()) stringResource(id = R.string.edit_address)
                    else stringResource(id = R.string.create_address),
                icon = if (!addressId.isNullOrEmpty()) Icons.Default.Edit else Icons.Default.Add,
                onClick = {
                    if (!addressId.isNullOrEmpty()) {
                        addEditAddressViewModel.onAddressEvent(
                            AddEditAddressEvent.UpdateAddress(
                                addressId
                            )
                        )
                    } else {
                        addEditAddressViewModel.onAddressEvent(AddEditAddressEvent.CreateNewAddress)
                    }
                }
            )
        }
    }
}