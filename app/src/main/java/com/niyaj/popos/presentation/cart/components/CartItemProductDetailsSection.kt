package com.niyaj.popos.presentation.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.domain.model.CartProduct
import com.niyaj.popos.presentation.ui.theme.LightColor10
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.util.toRupee

@Composable
fun CartItemProductDetailsSection(
    cartProducts: List<CartProduct>,
    decreaseQuantity: (String) -> Unit = {},
    increaseQuantity: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSmall)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(4.dp))
    ) {
        cartProducts.forEach { cartProduct ->
            if (cartProduct.product != null && cartProduct.quantity != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(SpaceMini),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(LightColor10, RoundedCornerShape(4.dp))
                            .weight(2.2f, true)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpaceMini),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(2.5f, true),
                                text = cartProduct.product.productName,
                                style = MaterialTheme.typography.subtitle1,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.width(SpaceMini))
                            Text(
                                text = cartProduct.product.productPrice.toString().toRupee,
                                modifier = Modifier.weight(0.5f, true),
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.8f, true),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = { decreaseQuantity(cartProduct.product.productId) },
                            ) {
                                Icon(
                                    imageVector = if (cartProduct.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.decreaseQuantity),
                                    tint = MaterialTheme.colors.secondaryVariant
                                )
                            }
                            Text(
                                text = cartProduct.quantity.toString(),
                                fontWeight = FontWeight.SemiBold,
                            )

                            IconButton(
                                onClick = { increaseQuantity(cartProduct.product.productId) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(id = R.string.increaseQuantity),
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}