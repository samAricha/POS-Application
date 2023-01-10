package com.niyaj.popos.features.main_feed.presentation.components.product

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.main_feed.data.repository.ProductWithFlowQuantity
import com.niyaj.popos.util.toRupee

@Composable
fun ProductItems(
    cartProducts: List<ProductWithFlowQuantity>,
    onLeftClick: (String) -> Unit = {},
    onRightClick: (String) -> Unit = {},
){
    LazyColumn{
        itemsIndexed(cartProducts){ index, productWithQuantity ->
            val quantity = productWithQuantity.quantity.collectAsState(0).value

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 1.dp,
                shape =  RoundedCornerShape(4.dp),
                backgroundColor = MaterialTheme.colors.surface,
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
                                onLeftClick(productWithQuantity.product.productId)
                            }
                    ) {
                        Row(modifier = Modifier
                            .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(SpaceSmall),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(
                                    text = productWithQuantity.product.productName,
                                    style = MaterialTheme.typography.body1,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = productWithQuantity.product.productPrice.toString().toRupee,
                                    style = MaterialTheme.typography.subtitle2,
                                    color = Color.Black
                                )
                            }
                            if(quantity != 0) {
                                Image(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.Gray),
                                )
                            }
                        }
                    }

                    Divider(
                        color = LightColor12,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onRightClick(productWithQuantity.product.productId)
                            }
                            .weight(1.5f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(quantity != 0) {
                            Image(
                                painterResource(id = R.drawable.ic_clear),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.h4,
                                color = Color.Gray
                            )
                        }else{
                            Image(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            if(index == cartProducts.size - 1) {
                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
            }
        }
    }

}