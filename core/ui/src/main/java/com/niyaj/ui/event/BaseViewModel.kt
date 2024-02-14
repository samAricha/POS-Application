package com.niyaj.ui.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(): ViewModel() {

    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    val mSearchText = mutableStateOf("")
    val searchText: State<String> = mSearchText

    val mSelectedItems  =  mutableStateListOf<String>()
    val selectedItems: SnapshotStateList<String> = mSelectedItems

    open var totalItems: List<String> = emptyList()

    val mEventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = mEventFlow.asSharedFlow()

    private var count: Int = 0

    open fun selectItem(itemId: String) {
        viewModelScope.launch {
            if(mSelectedItems.contains(itemId)){
                mSelectedItems.remove(itemId)
            }else{
                mSelectedItems.add(itemId)
            }
        }
    }

    open fun selectAllItems() {
        viewModelScope.launch {
            count += 1

            if (totalItems.isNotEmpty()){
                if (totalItems.size == mSelectedItems.size){
                    mSelectedItems.clear()
                }else{
                    totalItems.forEach { itemId ->
                        if (count % 2 != 0){
                            val selectedProduct = mSelectedItems.find { it == itemId }

                            if (selectedProduct == null){
                                mSelectedItems.add(itemId)
                            }
                        }else {
                            mSelectedItems.remove(itemId)
                        }
                    }
                }
            }
        }
    }

    open fun deselectItems() {
        mSelectedItems.clear()
    }

    open fun deleteItems() {}

    open fun openSearchBar() {
        viewModelScope.launch {
            _showSearchBar.emit(true)
        }
    }

    open fun searchTextChanged(text: String) {
        viewModelScope.launch {
            mSearchText.value = text
        }
    }

    open fun clearSearchText() {
        viewModelScope.launch {
            mSearchText.value = ""
        }
    }

    open fun closeSearchBar() {
        viewModelScope.launch {
            mSearchText.value = ""
            _showSearchBar.emit(false)
        }
    }

}