package com.niyaj.popos.features.delivery_partner.presentation.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.use_cases.PartnerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPartnerViewModel @Inject constructor(
    private val partnerUseCases: PartnerUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var addEditState by mutableStateOf(AddEditPartnerState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _passwordToggle = mutableStateOf(false)
    val passwordToggle: State<Boolean> = _passwordToggle

    var expanded by mutableStateOf(false)

    init {
        savedStateHandle.get<String>("partnerId")?.let { partnerId ->
            getPartnerById(partnerId)
        }
    }

    fun onAddEditPartnerEvent(event: AddEditPartnerEvent) {
        when (event){
            is AddEditPartnerEvent.PartnerNameChanged -> {
                addEditState = addEditState.copy(partnerName = event.partnerName)
            }

            is AddEditPartnerEvent.PartnerPhoneChanged -> {
                addEditState = addEditState.copy(partnerPhone = event.partnerPhone)
            }
            is AddEditPartnerEvent.PartnerEmailChanged -> {
                addEditState = addEditState.copy(partnerEmail = event.partnerEmail)
            }
            is AddEditPartnerEvent.PartnerPasswordChanged -> {
                addEditState = addEditState.copy(partnerPassword = event.partnerPassword)
            }
            is AddEditPartnerEvent.PartnerStatusChanged -> {
                addEditState = addEditState.copy(partnerStatus = event.partnerStatus)
            }
            is AddEditPartnerEvent.PartnerTypeChanged -> {
                addEditState = addEditState.copy(partnerType = event.partnerType)
            }

            is AddEditPartnerEvent.CreateNewPartner -> {
                addOrEditPartner()
            }
            is AddEditPartnerEvent.UpdatePartner -> {
                addOrEditPartner(event.partnerId)
            }
        }

    }

    fun togglePassword(status: Boolean){
        _passwordToggle.value = status
    }

    private fun addOrEditPartner(partnerId: String? = null){
        viewModelScope.launch {
            val validatedPartnerName = partnerUseCases.validatePartnerName(addEditState.partnerName)
            val validatedPartnerPhone = partnerUseCases.validatePartnerPhone(addEditState.partnerPhone, partnerId)
            val validatedPartnerEmail = partnerUseCases.validatePartnerEmail(addEditState.partnerEmail, partnerId)
            val validatedPartnerPassword = partnerUseCases.validatePartnerPassword(addEditState.partnerPassword)

            val hasError = listOf(validatedPartnerName, validatedPartnerPhone, validatedPartnerEmail, validatedPartnerPassword).any {
                !it.successful
            }

            if (hasError) {
                addEditState = addEditState.copy(
                    partnerNameError = validatedPartnerName.errorMessage,
                    partnerPhoneError = validatedPartnerPhone.errorMessage,
                    partnerEmailError = validatedPartnerEmail.errorMessage,
                    partnerPasswordError = validatedPartnerPassword.errorMessage,
                )

                return@launch
            }else {
                viewModelScope.launch {
                    val partner = DeliveryPartner()
                    partner.partnerName = addEditState.partnerName
                    partner.partnerPhone = addEditState.partnerPhone
                    partner.partnerEmail = addEditState.partnerEmail
                    partner.partnerPassword = addEditState.partnerPassword
                    partner.partnerStatus = addEditState.partnerStatus
                    partner.partnerType = addEditState.partnerType

                    if(partnerId.isNullOrEmpty()){
                        when(val result = partnerUseCases.createNewPartner(partner)){
                            is Resource.Loading -> {
                                _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Partner created successfully"))
                                addEditState = AddEditPartnerState()
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new employee"))
                            }
                        }

                    }else {
                        val result = partnerUseCases.updatePartner(partner, partnerId)
                        when(result){
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError( "Unable to Update Partner"))
                            }
                            is Resource.Loading -> {

                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Partner updated successfully"))
                                addEditState = AddEditPartnerState()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun getPartnerById(partnerId: String) {
        if(partnerId.isNotEmpty()) {
            viewModelScope.launch {
                when(val result = partnerUseCases.getPartnerById(partnerId)) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            addEditState = addEditState.copy(
                                partnerName = it.partnerName,
                                partnerEmail = it.partnerEmail,
                                partnerPhone = it.partnerPhone,
                                partnerPassword = it.partnerPassword,
                                partnerStatus = it.partnerStatus,
                                partnerType = it.partnerType,
                            )
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

}