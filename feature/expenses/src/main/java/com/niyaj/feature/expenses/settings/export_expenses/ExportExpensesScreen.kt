package com.niyaj.feature.expenses.settings.export_expenses

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOT_AVAILABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_FILE_NAME
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_TITLE
import com.niyaj.common.utils.Constants.ImportExportType.EXPORT
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.expenses.components.ImportExportExpensesBody
import com.niyaj.feature.expenses.destinations.AddEditExpensesScreenDestination
import com.niyaj.feature.expenses.settings.ExpensesSettingViewModel
import com.niyaj.feature.expenses.settings.ExpensesSettingsEvent
import com.niyaj.ui.components.ExportedFooter
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ExportExpensesScreen(
    navController: NavController,
    viewModel: ExpensesSettingViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(key1 = true, key2 = Unit) {
        scope.launch {
            viewModel.onEvent(ExpensesSettingsEvent.GetAllExpenses)
        }
    }

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    )

    val isChoose = viewModel.onChoose

    val expensesState = viewModel.state.collectAsStateWithLifecycle().value

    val selectedExpenses = viewModel.selectedExpenses.toList()

    val exportedData = viewModel.importExportedExpenses.collectAsStateWithLifecycle().value

    var expanded by remember {
        mutableStateOf(false)
    }

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeData(context, it, exportedData)

                    if (result) {
                        resultBackNavigator.navigateBack("${exportedData.size} Expenses has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export expenses")
                    }
                }
            }
        }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = EXPORT_EXPENSE_TITLE,
        icon = Icons.Default.Upload,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        if (expensesState.isNotEmpty()) {
            val showFileSelector = if (isChoose) selectedExpenses.isNotEmpty() else true
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ) {
                ImportExportHeader(
                    text = "Export " + if (isChoose) "${selectedExpenses.size} Selected Expenses" else " All Expenses",
                    isChosen = isChoose,
                    onClickChoose = {
                        viewModel.onEvent(ExpensesSettingsEvent.OnChooseExpenses)
                    },
                    onClickAll = {
                        viewModel.onChoose = false
                        viewModel.onEvent(ExpensesSettingsEvent.DeselectExpenses)
                    },
                )

                AnimatedVisibility(
                    visible = isChoose,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (expanded) Modifier.weight(1.1F) else Modifier),
                ) {
                    ImportExportExpensesBody(
                        lazyListState = lazyListState,
                        expenses = expensesState,
                        selectedExpenses = selectedExpenses,
                        expanded = expanded,
                        onExpandChanged = {
                            expanded = !expanded
                        },
                        onSelectExpense = {
                            viewModel.onEvent(ExpensesSettingsEvent.SelectExpenses(it))
                        },
                        onClickSelectAll = {
                            viewModel.onEvent(ExpensesSettingsEvent.SelectAllExpenses(EXPORT))
                        }
                    )
                }

                ExportedFooter(
                    text = EXPORT_EXPENSE_TITLE,
                    showFileSelector = showFileSelector,
                    onExportClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.createFile(
                                context = context,
                                fileName = EXPORT_EXPENSE_FILE_NAME
                            )
                            exportLauncher.launch(result)
                            viewModel.onEvent(ExpensesSettingsEvent.GetExportedExpenses)
                        }
                    }
                )
            }
        } else {
            ItemNotAvailable(
                text = EXPENSE_NOT_AVAILABLE,
                buttonText = CREATE_NEW_EXPENSE,
                onClick = {
                    navController.navigate(AddEditExpensesScreenDestination())
                }
            )
        }
    }
}