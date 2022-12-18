package com.niyaj.popos.presentation.settings.print_setting

data class BluetoothDeviceState(
    val name: String? = "",
    val address: String? = "",
    val bondState: Int? = null,
    val type: Int? = null,
    val connected: Boolean = false,
)
