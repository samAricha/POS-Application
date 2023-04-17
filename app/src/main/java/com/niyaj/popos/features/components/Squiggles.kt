package com.niyaj.popos.features.components

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niyaj.popos.features.common.ui.theme.BabyBlue
import com.niyaj.popos.features.common.ui.theme.LimeGreen
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.ui.theme.SeafoamGreen
import kotlin.math.roundToInt
import kotlin.math.sin

const val PI = Math.PI.toFloat()
fun Int.pi() = this * PI

@Preview(showBackground = true)
@Composable
fun RebuildingSinaSquiggles() {
    val infiniteTransition = rememberInfiniteTransition()
    val wave by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val showPoints by remember { mutableStateOf(true) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val wavelength = 32.dp.toPx()
        val amplitude = 8.dp.toPx()
        val segment = wavelength / 4f
        val centerY = size.height / 2
        val points = mutableListOf<Offset>()

        val step = wave * wavelength

        var distance = 0f
        val path = Path().apply {
            reset()
            moveTo(step + 0f, centerY)
            points.add(Offset(step + 0f, centerY))
            while (distance < (size.width + wavelength)) {
                val x1 = segment + distance + step
                val x2 = segment * 2 + distance + step
                val x3 = segment * 3 + distance + step
                val x4 = segment * 4 + distance + step

                val y1 = centerY - amplitude
                val y2 = centerY
                val y3 = centerY + amplitude
                val y4 = centerY

                points.add(Offset(x1, y1))
                points.add(Offset(x2, y2))
                points.add(Offset(x3, y3))
                points.add(Offset(x4, y4))

                quadraticBezierTo(x1, y1, x2, y2)
                quadraticBezierTo(x3, y3, x4, y4)
                distance += wavelength
            }
        }

        clipRect {
            drawPath(path = path, color = SeafoamGreen, style = Stroke(width = 15f, cap = StrokeCap.Round))
            if (showPoints) {
                points.forEach {
                    drawCircle(color = Color.White, radius = 2f, center = it)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RebuildingSaketSquiggles() {
    val infiniteTransition = rememberInfiniteTransition()
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val showPoints by remember { mutableStateOf(false) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {

        /*
        general form of a sine function
        y = a*sin(b*x - c) + d

        a = amplitude (wave height)
        b = stretch - 2pi / wavelength
        c = phase (x) shift
        d = vertical (y) shift
        */
        val wavelength = 32.dp.toPx()
        val amplitude = 4.dp.toPx()
        val yShift = size.height / 2
        val stretch = 2.pi() / wavelength
        val xShift = wave * 2.pi()

        fun sinY(x: Float): Float {
            return amplitude * sin(stretch * x - xShift) + yShift
        }

        val horizontalPadding = 16.dp.toPx()
        val actualWidth = size.width - horizontalPadding * 2
        val segmentLength = wavelength / 10f
        val numSegments = (actualWidth / segmentLength).roundToInt()

        val collectedPoints = mutableListOf<Offset>()

        var pointX = horizontalPadding
        val path = Path().apply {
            for (segment in 0..numSegments) {
                val pointY = sinY(pointX)

                when (segment) {
                    0 -> moveTo(pointX, pointY)
                    else -> lineTo(pointX, pointY)
                }

                collectedPoints.add(Offset(pointX, pointY))
                pointX += segmentLength
            }
        }

        drawPath(
            path = path,
            color = BabyBlue,
            style = Stroke(
                width = 15f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(radius = amplitude)
            )
        )
        if (showPoints) {
            collectedPoints.forEach {
                drawCircle(color = Color.Black, radius = 2f, center = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PathEffectSquiggles() {
    val infiniteTransition = rememberInfiniteTransition()
    val phaseMultiplier by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val wavelength = 32.dp.toPx()
        val amplitude = 8.dp.toPx()
        val step = wavelength / 4
        val phase = wavelength * phaseMultiplier
        val centerY = size.height / 2
        val waveWidth = size.width + wavelength

        val wavePath = Path().apply {
            moveTo(-wavelength, 0f)
            relativeQuadraticBezierTo(step, amplitude, step * 2, 0f)
            relativeQuadraticBezierTo(step, -amplitude, step * 2, 0f)
        }

        val stampPath = Path()

        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 15f
            strokeCap = Paint.Cap.ROUND
            getFillPath(wavePath.asAndroidPath(), stampPath.asAndroidPath())
        }

        clipRect {
            drawLine(
                color = LimeGreen,
                start = Offset(-wavelength, centerY),
                end = Offset(waveWidth, centerY),
                strokeWidth = 0f,
                pathEffect = PathEffect.stampedPathEffect(
                    shape = stampPath,
                    advance = wavelength,
                    phase = -phase,
                    style = StampedPathEffectStyle.Morph
                )
            )
        }
    }
}

@Composable
fun Squiggles() {
    PoposTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = LimeGreen)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = "Bezier Curves", color = LimeGreen, fontSize = 16.sp)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    RebuildingSinaSquiggles()
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = "Sin Wave", color = BabyBlue, fontSize = 16.sp)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    RebuildingSaketSquiggles()
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = "Line + Stamp Path Effect", color = LimeGreen, fontSize = 16.sp)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PathEffectSquiggles()
                }
            }
        }
    }
}