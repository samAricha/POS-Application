package com.niyaj.popos.features.components.chart.horizontalbar.common

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData

internal fun getTopLeft(
    index: Int,
    barHeight: MutableState<Float>,
    size: Size,
    horizontalBarData: HorizontalBarData,
    scalableFactor: Float
) = Offset(
    y = index.times(barHeight.value.times(1.2F)),
    x = size.width.minus(horizontalBarData.xValue.times(scalableFactor))
)

internal fun getBottomLeft(
    index: Int,
    barHeight: MutableState<Float>,
    size: Size,
    horizontalBarData: HorizontalBarData,
    scalableFactor: Float
) = Offset(
    y = index.plus(1).times(barHeight.value.times(1.2F)),
    x = size.width.minus(horizontalBarData.xValue.times(scalableFactor))

)
