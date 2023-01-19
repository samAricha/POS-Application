package com.niyaj.popos.features.addon_item.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BY_DATE
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BY_ID
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BY_NAME
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_FILTER_BY_PRICE
import com.niyaj.popos.features.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.components.FilterItem
import com.niyaj.popos.util.Constants.SORT_ASCENDING
import com.niyaj.popos.util.Constants.SORT_DESCENDING

@Composable
fun FilterAddOnItemScreen(
    onClosePressed: () -> Unit,
    filterAddOnItem: FilterAddOnItem,
    onFilterChanged: (FilterAddOnItem) -> Unit,
) {

    val selectedColor: Color = MaterialTheme.colors.secondary
    val unselectedColor: Color = Teal200

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterItem(
                modifier = Modifier
                    .testTag(SORT_ASCENDING)
                    .weight(1.2f)
                    .height(40.dp),
                filterName = "Ascending",
                color = if(filterAddOnItem.sortType is SortType.Ascending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddOnItem.sortType is SortType.Ascending,
                onSelected = {
                    onFilterChanged(filterAddOnItem.copy(SortType.Ascending))
                    onClosePressed()
                },
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            FilterItem(
                modifier = Modifier
                    .testTag(SORT_DESCENDING)
                    .weight(1.2f)
                    .height(40.dp),
                filterName = "Descending",
                color = if(filterAddOnItem.sortType is SortType.Descending) MaterialTheme.colors.primary else unselectedColor,
                itemSelected = filterAddOnItem.sortType is SortType.Descending,
                onSelected = {
                    onFilterChanged(filterAddOnItem.copy(SortType.Descending))
                    onClosePressed()
                },
            )
        }

        Spacer(modifier = Modifier.height(SpaceMedium))

        FilterItem(
            modifier = Modifier
                .testTag(ADDON_FILTER_BY_ID)
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By ID",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemId) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemId,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemId(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .testTag(ADDON_FILTER_BY_NAME)
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Name",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemName) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemName,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemName(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .testTag(ADDON_FILTER_BY_PRICE)
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Price",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemPrice) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemPrice,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemPrice(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        FilterItem(
            modifier = Modifier
                .testTag(ADDON_FILTER_BY_DATE)
                .fillMaxWidth()
                .height(40.dp),
            filterName = "Sort By Date",
            color = if(filterAddOnItem is FilterAddOnItem.ByAddOnItemDate) selectedColor else unselectedColor,
            itemSelected = filterAddOnItem is FilterAddOnItem.ByAddOnItemDate,
            onSelected = {
                onFilterChanged(FilterAddOnItem.ByAddOnItemDate(filterAddOnItem.sortType))
                onClosePressed()
            },
        )
    }
}