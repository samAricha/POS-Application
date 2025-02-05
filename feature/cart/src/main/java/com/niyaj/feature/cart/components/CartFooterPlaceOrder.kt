package com.niyaj.feature.cart.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun CartFooterPlaceOrder(
    modifier: Modifier = Modifier,
    countTotalItems: Int,
    countSelectedItem: Int,
    showPrintBtn: Boolean = true,
    onClickSelectAll: () -> Unit = {},
    onClickPlaceAllOrder: () -> Unit = {},
    onClickPrintAllOrder: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = if (countTotalItems == countSelectedItem)
                            Icons.Default.CheckCircle
                        else Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = if (countTotalItems == countSelectedItem) MaterialTheme.colors.primary
                        else MaterialTheme.colors.secondary
                    )
                }

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("$countTotalItems")
                            append(" - ")
                        }

                        append("$countSelectedItem")

                        append(" Selected")
                    },
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClickSelectAll
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text =
                    if (countSelectedItem == 0) " " else if (countSelectedItem < countTotalItems) " $countSelectedItem " else " All "
                Button(
                    onClick = onClickPlaceAllOrder,
                    enabled = countSelectedItem > 0,
                    shape = CutCornerShape(4.dp)
                ) {
                    Text(
                        text = "Place${text}Order".uppercase(),
                        style = MaterialTheme.typography.button
                    )
                }

                if (showPrintBtn) {
                    Spacer(modifier = Modifier.width(SpaceSmall))

                    Button(
                        onClick = onClickPrintAllOrder,
                        enabled = countSelectedItem > 0,
                        shape = CutCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondaryVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print Order",
                            tint = MaterialTheme.colors.onSecondary,
                        )
                    }
                }
            }
        }
    }
}