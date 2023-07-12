package com.niyaj.popos.features.expenses.presentation.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.features.expenses_category.domain.use_cases.GetAllExpensesCategory
import com.niyaj.popos.features.expenses_category.presentation.ExpensesCategoryState
import com.niyaj.popos.utils.getCalculatedStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AddEditExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    private val getAllExpensesCategory : GetAllExpensesCategory,
    private val validationRepository : ExpensesValidationRepository,
    private val expensesCategoryRepository: ExpensesCategoryRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _addEditState = mutableStateOf(AddEditExpensesState())
    val addEditState: State<AddEditExpensesState> = _addEditState

    private val _expensesCategories = MutableStateFlow(ExpensesCategoryState())
    val expensesCategories = _expensesCategories.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("expensesId")?.let { expensesId ->
            getExpensesById(expensesId)
        }

        getAllExpensesCategory()
    }

    /**
     *
     */
    fun onExpensesEvent(event: AddEditExpensesEvent) {
        when(event) {
            is AddEditExpensesEvent.ExpensesCategoryNameChanged -> {
                viewModelScope.launch {
                    val result = expensesCategoryRepository.getExpensesCategoryById(event.expensesCategoryId).data

                    result?.let {
                        _addEditState.value = _addEditState.value.copy(
                            expensesCategory = it
                        )
                    }
                }
            }

            is AddEditExpensesEvent.ExpensesPriceChanged -> {
                _addEditState.value = _addEditState.value.copy(expensesPrice = event.expensesPrice)
            }

            is AddEditExpensesEvent.ExpensesRemarksChanged -> {
                _addEditState.value = _addEditState.value.copy(expensesRemarks = event.expensesRemarks)
            }

            is AddEditExpensesEvent.ExpensesDateChanged -> {
                _addEditState.value = _addEditState.value.copy(expensesGivenDate = event.expensesGivenDate)
            }

            is AddEditExpensesEvent.CreateNewExpenses -> {
                addOrEditExpenses()
            }

            is AddEditExpensesEvent.UpdateExpenses -> {
                addOrEditExpenses(event.expensesId)
            }

            is AddEditExpensesEvent.OnSearchExpensesCategory -> {
                viewModelScope.launch {
                    getAllExpensesCategory(searchText = event.searchText)
                }
            }
        }
    }

    private fun addOrEditExpenses(expensesId: String? = null){
        val validatedExpensesCategory = validationRepository.validateExpensesCategory(_addEditState.value.expensesCategory.expensesCategoryId)

        val validatedExpensesPrice = validationRepository.validateExpensesPrice(_addEditState.value.expensesPrice)

        val hasError = listOf(validatedExpensesCategory, validatedExpensesPrice).any {
            !it.successful
        }

        if (hasError) {
            _addEditState.value = addEditState.value.copy(
                expensesCategoryError = validatedExpensesCategory.errorMessage,
                expensesPriceError = validatedExpensesPrice.errorMessage,
            )
            return
        }else {
            viewModelScope.launch {
                if(expensesId.isNullOrEmpty()){
                    val result = expensesRepository.createNewExpenses(
                        Expenses(
                            expensesCategory = _addEditState.value.expensesCategory,
                            expensesPrice = _addEditState.value.expensesPrice,
                            expensesRemarks = _addEditState.value.expensesRemarks,
                            createdAt = _addEditState.value.expensesGivenDate,
                        )
                    )
                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success(result.message ?: "Expenses created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to create new expenses"))
                        }
                    }

                }else {
                    val result = expensesRepository.updateExpenses(
                        Expenses(
                            expensesCategory = _addEditState.value.expensesCategory,
                            expensesPrice = _addEditState.value.expensesPrice,
                            expensesRemarks = _addEditState.value.expensesRemarks,
                            createdAt = getCalculatedStartDate(date = _addEditState.value.expensesGivenDate),
                        ),
                        expensesId
                    )
                    when(result){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error( "Unable to Update Expenses"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Expenses updated successfully"))
                        }
                    }
                }
            }

            _addEditState.value = AddEditExpensesState()
        }
    }

    private fun getExpensesById(expensesId: String) {
        viewModelScope.launch {
            when(val result = expensesRepository.getExpensesById(expensesId)) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    result.data?.let {
                        if (it.expensesCategory != null){
                            _addEditState.value = addEditState.value.copy(
                                expensesCategory = it.expensesCategory!!,
                                expensesPrice = it.expensesPrice,
                                expensesRemarks = it.expensesRemarks,
                                expensesGivenDate = it.createdAt
                            )
                        }
                    }
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun getAllExpensesCategory(searchText : String = "") {
        viewModelScope.launch {
            getAllExpensesCategory.invoke(searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _expensesCategories.value = _expensesCategories.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _expensesCategories.value = _expensesCategories.value.copy(
                                expensesCategory = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _expensesCategories.value = _expensesCategories.value.copy(error = "Unable to load resources")
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

}