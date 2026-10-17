package com.sberkids.ht.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R

/**
 * The mock is drawn in Inter (confirmed by the TEXT nodes in the Figma file:
 * fontFamily "Inter", fontWeight 400). Roboto, the platform default, renders
 * noticeably thinner at the same weight, so the whole app uses Inter.
 */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
)

private val LightColors = lightColorScheme(
    primary = SberGreen,
    onPrimary = Color.White,
)

private fun style(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = Inter,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
)

private val AppTypography = Typography(
    displaySmall = style(34, 40, FontWeight.Bold),
    headlineSmall = style(22, 28, FontWeight.Bold),
    titleLarge = style(20, 26, FontWeight.Bold),
    titleMedium = style(16, 22, FontWeight.SemiBold),
    bodyLarge = style(15, 20, FontWeight.Medium),
    bodyMedium = style(14, 18, FontWeight.Medium),
    bodySmall = style(13, 16, FontWeight.Medium),
    labelLarge = style(14, 18, FontWeight.SemiBold),
    labelMedium = style(12, 14, FontWeight.SemiBold),
    labelSmall = style(10, 12, FontWeight.SemiBold),
)

@Composable
fun SberKidsHtTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
