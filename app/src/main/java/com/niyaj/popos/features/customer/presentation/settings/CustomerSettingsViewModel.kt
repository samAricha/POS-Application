package com.niyaj.popos.features.customer.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.Constants
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.use_cases.GetAllCustomers
import com.niyaj.popos.features.customer.presentation.CustomerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is the ViewModel class for the CustomerSettingsScreen
 */
@HiltViewModel
class CustomerSettingsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val getAllCustomers : GetAllCustomers
): ViewModel() {

    private val _customers = MutableStateFlow(CustomerState())
    val customers = _customers.asStateFlow()

    private val _selectedCustomers = mutableStateListOf<String>()
    val selectedCustomers: SnapshotStateList<String> = _selectedCustomers

    private val _importExportedCustomers = MutableStateFlow<List<Customer>>(emptyList())
    val importExportedCustomers = _importExportedCustomers.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 0

    /**
     * This function is used to handle the events from the UI
     */
    fun onEvent(event: CustomerSettingsEvent){
        when (event) {
            is CustomerSettingsEvent.SelectCustomer -> {
                viewModelScope.launch {
                    if(_selectedCustomers.contains(event.customerId)){
                        _selectedCustomers.remove(event.customerId)
                    }else{
                        _selectedCustomers.add(event.customerId)
                    }
                }
            }

            is CustomerSettingsEvent.SelectAllCustomer -> {
                count += 1

                val contacts = when(event.type) {
                    Constants.ImportExportType.IMPORT -> _importExportedCustomers.value
                    Constants.ImportExportType.EXPORT -> _customers.value.customers
                }

                if (contacts.isNotEmpty()){
                    contacts.forEach { customer ->
                        if (count % 2 != 0){

                            val selectedContact = _selectedCustomers.find { it == customer.customerId }

                            if (selectedContact == null){
                                _selectedCustomers.add(customer.customerId)
                            }
                        }else {
                            _selectedCustomers.remove(customer.customerId)
                        }
                    }
                }
            }

            is CustomerSettingsEvent.DeselectCustomers -> {
                _selectedCustomers.clear()
            }

            is CustomerSettingsEvent.OnChooseCustomer -> {
                onChoose = !onChoose
            }

            is CustomerSettingsEvent.ImportCustomers -> {
                val customers = mutableStateListOf<Customer>()

                _selectedCustomers.forEach {
                    val data = _importExportedCustomers.value.find { customer -> customer.customerId == it }
                    if (data != null) customers.add(data)
                }

                viewModelScope.launch {
                    when (val result = customerRepository.importContacts(customers.toList())){
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("${customers.toList().size} customers imported successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to import customers"))
                        }
                    }
                }
            }

            is CustomerSettingsEvent.ImportCustomerData -> {
                _importExportedCustomers.value = emptyList()

                if (event.customers.isNotEmpty()) {
                    _importExportedCustomers.value = event.customers

                    _selectedCustomers.addAll(event.customers.map { it.customerId })
                }
            }

            is CustomerSettingsEvent.ClearImportedCustomer -> {
                _importExportedCustomers.value = emptyList()
                _selectedCustomers.clear()
                onChoose = false
            }

            is CustomerSettingsEvent.GetAllCustomer -> {
                getAllCustomers()
            }

            is CustomerSettingsEvent.GetExportedCustomer -> {
                viewModelScope.launch {
                    if (_selectedCustomers.isEmpty()){
                        _importExportedCustomers.emit(_customers.value.customers)
                    } else {
                        val customers = mutableListOf<Customer>()

                        _selectedCustomers.forEach { id ->
                            val customer = _customers.value.customers.find { it.customerId == id }
                            if (customer != null){
                                customers.add(customer)
                            }
                        }

                        _importExportedCustomers.emit(customers.toList())
                    }
                }
            }

            is CustomerSettingsEvent.DeleteAllCustomer -> {
                viewModelScope.launch {
                    when(val result = customerRepository.deleteAllCustomer()){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("All Customers Deleted Successfully."))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete all customers"))
                        }
                    }
                }
            }

        }
    }

    private fun getAllCustomers() {
        getAllCustomers.invoke().onEach { result ->
            when(result){
                is Resource.Loading -> {
                    _customers.value = _customers.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let { customers ->
                        _customers.value = _customers.value.copy(
                            customers = customers,
                        )
                    }
                }
                is Resource.Error -> {
                    _customers.value = _customers.value.copy(
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}