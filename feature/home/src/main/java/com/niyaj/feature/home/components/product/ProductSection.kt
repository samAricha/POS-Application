package com.niyaj.feature.home.components.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.utils.toRupee
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.IconSizeMedium
import com.niyaj.designsystem.theme.PoposPink100
import com.niyaj.designsystem.theme.ProfilePictureSizeSmall
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.home.components.components.TitleWithIcon
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.util.isScrolled
import kotlinx.coroutines.launch

@Composable
fun ProductSection(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    products: List<ProductWithFlowQuantity>,
    isLoading: Boolean = false,
    onProductFilterClick: () -> Unit = {},
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
    onNavigateToProductScreen: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    if (isLoading) {
        LoadingIndicator()
    } else if (products.isNotEmpty()) {
        TitleWithIcon(
            text = "Products",
            icon = Icons.Default.Dns,
            showScrollToTop = lazyListState.isScrolled,
            onClick = {
                onProductFilterClick()
            },
            onClickScrollToTop = {
                scope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        ProductItems(
            modifier = modifier,
            lazyListState = lazyListState,
            cartProducts = products,
            onProductLeftClick = { product ->
                onProductLeftClick(product)
            },
            onProductRightClick = { product ->
                onProductRightClick(product)
            },
        )
    } else {
        ItemNotAvailable(
            text = PRODUCT_NOT_AVAILABLE,
            image = R.drawable.nothinghere,
            buttonText = CREATE_NEW_PRODUCT,
            onClick = onNavigateToProductScreen
        )
    }
}

@Composable
fun ProductItems(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    cartProducts: List<ProductWithFlowQuantity>,
    backgroundColor: Color = MaterialTheme.colors.surface,
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
) {
    LazyColumn(
        state = lazyListState,
    ) {
        itemsIndexed(
            items = cartProducts,
            key = { _, product ->
                product.productId
            }
        ) { index, product ->
            val quantity = product.quantity.collectAsStateWithLifecycle(0).value

            Card(
                modifier = modifier
                    .fillMaxWidth(),
                elevation = 1.dp,
                shape = RoundedCornerShape(4.dp),
                backgroundColor = backgroundColor,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable(
                                enabled = quantity != 0
                            ) {
                                onProductLeftClick(product.productId)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(SpaceSmall),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = product.productName,
                                    style = MaterialTheme.typography.body1,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = product.productPrice.toString().toRupee,
                                    style = MaterialTheme.typography.subtitle2,
                                    color = Color.Black
                                )
                            }

                            if (quantity != 0) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                    }

                    Divider(
                        color = PoposPink100,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onProductRightClick(product.productId)
                            }
                            .weight(1.5f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (quantity != 0) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            if (index == cartProducts.size - 1) {
                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
            }
        }
    }
}


const val PRODUCT_NOT_AVAILABLE = "You did not add any product yet, click below button to create new products"
