package com.niyaj.feature.customer.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.common.tags.CustomerTestTags
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SETTINGS
import com.niyaj.common.tags.CustomerTestTags.DELETE_ALL_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_TITLE
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.customer.destinations.ExportCustomerScreenDestination
import com.niyaj.feature.customer.destinations.ImportContactScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

/**
 * Customer Settings Screen
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param importRecipient
 * @see CustomerSettingsViewModel
 * @author Sk Niyaj Ali
 */
@Destination
@Composable
fun CustomerSettingsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
    importRecipient: ResultRecipient<ImportContactScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportCustomerScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }



    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        navActions = {},
        title = {
            Text(text = CUSTOMER_SETTINGS)
        },
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            ScrollToTop(
                visible = lazyListState.isScrolled,
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpaceSmall))

                SettingsCard(
                    text = DELETE_ALL_CUSTOMER,
                    icon = Icons.Default.DeleteForever,
                    onClick = {
                        dialogState.show()
                    },
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item {
                SettingsCard(
                    text = IMPORT_CUSTOMER_TITLE,
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportContactScreenDestination())
                    },
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item {
                SettingsCard(
                    text = EXPORT_CUSTOMER_TITLE,
                    icon = Icons.Default.SaveAlt,
                    iconModifier = Modifier.rotate(180F),
                    onClick = {
                        navController.navigate(ExportCustomerScreenDestination())
                    },
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.onEvent(CustomerSettingsEvent.DeleteAllCustomer)
                }
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    dialogState.hide()
                },
            )
        }
    ) {
        title(text = CustomerTestTags.DELETE_CUSTOMER_TITLE)
        message(text = CustomerTestTags.DELETE_CUSTOMER_MESSAGE)
    }

}