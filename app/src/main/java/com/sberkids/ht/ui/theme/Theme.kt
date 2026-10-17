package com.sberkids.ht.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R

/**
 * The mock is Inter at a weight past any named static instance: ink coverage is
 * 0.363 at 14sp and 0.375 at 23sp, while Bold gives 0.311 / 0.327 and ExtraBold
 * 0.328 / 0.367. A baked wght 880 reproduces both, with the exact 269x13 and
 * 200x20 boxes.
 *
 * Faces are pre-instantiated per optical size instead of shipping a variable
 * font. Android interpolates variable outlines while drawing, and that is what
 * left the stems ragged at small sizes; static outlines only get rasterised.
 */
const val MOCK_WEIGHT = 900

/** Letter spacing on the mock: +0.2dp at 14sp and +0.8dp at 23sp, ~1.5% of em. */
const val MOCK_SPACING_EM = 0.015f

private fun opszFor(px: Int): Int = when {
    px <= 15 -> 14
    px <= 19 -> 18
    px <= 27 -> 24
    else -> 32
}

private val Face14 = FontFamily(Font(R.font.inter_w900_o14, weight = FontWeight(MOCK_WEIGHT)))
private val Face18 = FontFamily(Font(R.font.inter_w900_o18, weight = FontWeight(MOCK_WEIGHT)))
private val Face24 = FontFamily(Font(R.font.inter_w900_o24, weight = FontWeight(MOCK_WEIGHT)))
private val Face32 = FontFamily(Font(R.font.inter_w900_o32, weight = FontWeight(MOCK_WEIGHT)))

private fun faceFor(px: Int): FontFamily = when (opszFor(px)) {
    14 -> Face14
    18 -> Face18
    24 -> Face24
    else -> Face32
}

/** Default face for text that does not set a size. */
val Inter14: FontFamily = Face14

/** Picks the optical size that matches the type size instead of one global face. */
@Composable
fun rememberInterFor(sizePx: Number): FontFamily =
    remember(sizePx.toInt()) { faceFor(sizePx.toInt()) }

private val LightColors = lightColorScheme(
    primary = SberGreen,
    onPrimary = Color.White,
)

private fun style(size: Int, lineHeight: Int) = TextStyle(
    fontFamily = faceFor(size),
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = FontWeight(MOCK_WEIGHT),
    letterSpacing = MOCK_SPACING_EM.em,
)

private val AppTypography = Typography(
    displaySmall = style(34, 40),
    headlineSmall = style(22, 28),
    titleLarge = style(20, 26),
    titleMedium = style(16, 22),
    bodyLarge = style(15, 20),
    bodyMedium = style(14, 18),
    bodySmall = style(13, 16),
    labelLarge = style(14, 18),
    labelMedium = style(12, 14),
    labelSmall = style(10, 12),
)

@Composable
fun SberKidsHtTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
