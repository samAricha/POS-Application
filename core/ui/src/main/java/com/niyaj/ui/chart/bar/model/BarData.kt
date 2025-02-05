package com.niyaj.ui.chart.bar.model

data class BarData(val xValue: Any, val yValue: Float)

internal fun List<BarData>.maxYValue() = maxOf {
    it.yValue
}
