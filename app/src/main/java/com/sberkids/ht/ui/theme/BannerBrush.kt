package com.sberkids.ht.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Banner gradient, reproduced from the Figma node `1:10635`.
 *
 * Figma stores the two handles in normalised node coordinates:
 *   start (1.0899, 1.1923) -> end (0.9475, -0.8635)
 * i.e. the axis runs from below the bottom-right corner upwards. The stops are
 * green at 0.0, blue at 0.529 and violet at 1.0, so the violet end lands at the
 * top and the green end just below the bottom edge.
 *
 * The handles must be multiplied by the *pixel* size of the composable, not by
 * its dp size: Brush.linearGradient takes px. Passing dp-scaled values shrinks
 * the axis by the display density factor and visibly distorts the ramp.
 */
fun bannerBrush(widthPx: Float, heightPx: Float): Brush = Brush.linearGradient(
    colorStops = BannerColorStops,
    start = Offset(1.0899f * widthPx, 1.1923f * heightPx),
    end = Offset(0.9475f * widthPx, -0.8635f * heightPx),
)

val BannerColorStops: Array<Pair<Float, Color>> = arrayOf(
    0f to Color(0xFF7CEC9F),
    0.529f to Color(0xFF67A6E8),
    1f to Color(0xFF9A73F0),
)


/**
 * Button fill used by the piggy CTA and the mini button in the completed cell,
 * taken from node `28:3485`: cyan -> mint -> yellow -> lime.
 */
val ButtonGradient: Array<Pair<Float, Color>> = arrayOf(
    0f to Color(0xFF4AD6FC),
    0.26f to Color(0xFF85FEDC),
    0.51f to Color(0xFFCDFF73),
    1f to Color(0xFF84FF20),
)
