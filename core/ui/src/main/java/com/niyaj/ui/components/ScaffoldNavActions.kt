package com.niyaj.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.niyaj.common.utils.Constants.SEARCH_ITEM_PLACEHOLDER
import com.niyaj.core.ui.R


/**
 * @param multiSelect: Whether to select multiple items
 * @param allItemsIsEmpty : if all items are empty, then show search icon
 * @param selectedItem : if selected items are empty, then show search icon
 * @param onClickEdit : edit button click listener (only visible when one item is selected)
 * @param onClickDelete : delete button click listener (only visible when one or more items are selected)
 * @param showSearchBar : show search bar
 * @param searchText : search text
 * @param onSearchTextChanged : search text change listener (only visible when search bar is visible)
 * @param onClearClick : clear button click listener (only visible when search bar is visible)
 * @param onClickSearch : search button click listener (only visible when search bar is not visible)
 * @param showSettingsIcon: show settings icon
 * @param onClickSetting: on setting click listener (only visible when show setting icon is visible)
 * @param content : additional content
 * @param preActionContent : pre action content
 * @param postActionContent: post action content
 */
@Composable
fun ScaffoldNavActions(
    multiSelect: Boolean,
    allItemsIsEmpty : Boolean = true,
    selectedItem : String = "",
    selectedItems : List<String> = emptyList(),
    onClickEdit : () -> Unit = {},
    onClickDelete : () -> Unit = {},
    onClickSelectAll : () -> Unit = {},
    showSearchBar : Boolean = false,
    searchText : String = "",
    onSearchTextChanged : (String) -> Unit = {},
    onClearClick : () -> Unit = {},
    onClickSearch : () -> Unit = {},
    showSettingsIcon : Boolean = false,
    onClickSetting : () -> Unit = {},
    content: @Composable () -> Unit = {},
    preActionContent: @Composable () -> Unit = {},
    postActionContent: @Composable () -> Unit = {},
) {
    val showActions = if (multiSelect) selectedItems.isNotEmpty() else selectedItem.isNotEmpty()

    if (showActions) {
        preActionContent()

        if (multiSelect) {
            if (selectedItems.size == 1) {
                IconButton(
                    onClick = onClickEdit,
                    modifier = Modifier.testTag(NAV_EDIT_BTN)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        }else {
            IconButton(
                onClick = onClickEdit,
                modifier = Modifier.testTag(NAV_EDIT_BTN)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Item",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }

        IconButton(
            onClick = onClickDelete,
            modifier = Modifier.testTag(NAV_DELETE_BTN)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Item",
                tint = MaterialTheme.colors.onPrimary,
            )
        }

        if (multiSelect) {
            IconButton(
                onClick = onClickSelectAll,
                modifier = Modifier.testTag(NAV_SELECT_ALL_BTN)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Rule,
                    contentDescription = "Select All Item",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }

        postActionContent()
    } else if (showSearchBar) {
        StandardSearchBar(
            modifier = Modifier.testTag(NAV_SEARCH_BAR),
            searchText = searchText,
            placeholderText = "Search for items...",
            onSearchTextChanged = {
                onSearchTextChanged(it)
            },
            onClearClick = onClearClick,
        )
    } else {
        if (!allItemsIsEmpty) {
            IconButton(
                onClick = onClickSearch,
                modifier = Modifier.testTag(NAV_SEARCH_BTN)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search_icon),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }

        if (showSettingsIcon) {
            IconButton(
                onClick = onClickSetting,
                modifier = Modifier.testTag(NAV_SETTING_BTN)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.setting_icon),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }

        content()
    }
}


/**
 * @param placeholderText: Show placeholder text into search bar
 * @param showSearchIcon : if all items are empty, then show search icon
 * @param selectionCount : if selected items are empty, then show search icon
 * @param onEditClick : edit button click listener (only visible when one item is selected)
 * @param onDeleteClick : delete button click listener (only visible when one or more items are selected)
 * @param showSearchBar : show search bar
 * @param searchText : search text
 * @param onSearchTextChanged : search text change listener (only visible when search bar is visible)
 * @param onClearClick : clear button click listener (only visible when search bar is visible)
 * @param onSearchClick : search button click listener (only visible when search bar is not visible)
 * @param showSettingsIcon: show settings icon
 * @param onSettingsClick: on setting click listener (only visible when show setting icon is visible)
 * @param content : additional content
 * @param preActionContent : pre action content
 * @param postActionContent: post action content
 */
@Composable
fun ScaffoldNavActions(
    selectionCount: Int,
    showSearchIcon: Boolean,
    searchText: String,
    placeholderText: String = SEARCH_ITEM_PLACEHOLDER,
    showSearchBar: Boolean = false,
    showSettingsIcon: Boolean = false,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit,
    content: @Composable () -> Unit = {},
    preActionContent: @Composable () -> Unit = {},
    postActionContent: @Composable () -> Unit = {},
) {
    if (selectionCount != 0) {
        preActionContent()

        if (selectionCount == 1) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.testTag(NAV_EDIT_BTN)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Item",
                )
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.testTag(NAV_DELETE_BTN)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Item",
            )
        }

        IconButton(
            onClick = onSelectAllClick,
            modifier = Modifier.testTag(NAV_SELECT_ALL_BTN)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Rule,
                contentDescription = "Select All Item",
            )
        }

        postActionContent()
    } else if (showSearchBar) {
        StandardSearchBar(
            searchText = searchText,
            placeholderText = placeholderText,
            onClearClick = onClearClick,
            onSearchTextChanged = onSearchTextChanged
        )
    } else {
        if (showSearchIcon) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag(NAV_SEARCH_BTN),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        }

        if (showSettingsIcon) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag(NAV_SETTING_BTN)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        content()
    }
}

const val NAV_SEARCH_BAR = "navigation_search_bar"
const val NAV_SEARCH_BTN = "navigation_search_icon"
const val NAV_SELECT_ALL_BTN = "navigation_select_all"
const val NAV_DELETE_BTN = "navigation_delete_btn"
const val NAV_EDIT_BTN = "navigation_edit_btn"
const val NAV_SETTING_BTN = "navigation_settings_btn"
