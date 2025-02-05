package com.niyaj.feature.expenses_category.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.ExpensesCategoryRepository
import com.niyaj.data.repository.validation.ExpCategoryValidationRepository
import com.niyaj.model.ExpensesCategory
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpensesCategoryViewModel @Inject constructor(
    private val repository: ExpensesCategoryRepository,
    private val validationRepository: ExpCategoryValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val categoryId = savedStateHandle.get<String>("expensesCategoryId")

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var categoryName by mutableStateOf("")

    val nameError = snapshotFlow { categoryName }.mapLatest {
        validationRepository.validateName(it, categoryId).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        savedStateHandle.get<String>("expensesCategoryId")?.let {
            getExpensesCategoryById(it)
        }
    }

    fun updateCategoryName(category: String) {
        categoryName = category
    }

    private fun getExpensesCategoryById(categoryId: String) {
        viewModelScope.launch {
            val result = repository.getExpensesCategoryById(categoryId)

            result.data?.let {
                categoryName = it.expensesCategoryName
            }
        }
    }

    fun createOrUpdateCategory(categoryId: String) {
        viewModelScope.launch {
            if (nameError.value == null) {
                val newCategory = ExpensesCategory(
                    expensesCategoryId = categoryId,
                    expensesCategoryName = categoryName.trim().capitalizeWords,
                )

                val message = if (categoryId.isEmpty()) "Created" else "Updated"

                when (val result = repository.createOrUpdateCategory(newCategory, categoryId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Category $message Successfully"))
                    }
                }
            }
        }
    }
}