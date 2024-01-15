package com.niyaj.popos.features.printer_info.domain.repository

import com.niyaj.popos.common.utils.Constants.PRINTER_ID
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.printer_info.domain.model.Printer
import kotlinx.coroutines.flow.Flow

interface PrinterRepository {

    fun getPrinter(printerId : String = PRINTER_ID): Printer

    fun getPrinterInfo(printerId: String = PRINTER_ID): Flow<Printer>

    suspend fun addOrUpdatePrinterInfo(printer : Printer): Resource<Boolean>
}