package com.niyaj.feature.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.feature.print.utils.BluetoothPrinter
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterInfoViewModel @Inject constructor(
    repository: PrinterRepository,
    private val bluetoothPrinter: BluetoothPrinter,
) : ViewModel() {

    val info = repository.getPrinterInfo()
        .map {
            if (it.printerId.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(it)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading
        )

    val printers = bluetoothPrinter.getBluetoothPrinters().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun connectPrinter(address: String) {
        viewModelScope.launch {
            bluetoothPrinter.connectBluetoothPrinter(address)
        }
    }

    fun testPrint() {
        viewModelScope.launch {
            bluetoothPrinter.printTestData()
        }
    }
}