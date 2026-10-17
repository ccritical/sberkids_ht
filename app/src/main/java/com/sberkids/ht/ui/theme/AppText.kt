package com.sberkids.ht.ui.theme

import androidx.compose.material3.Text as M3Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Drop-in replacement for material3 `Text` that keeps type on the pixel grid.
 *
 * Two things made text look ragged: a fractional em (cards scale their type by
 * the measured width, so sizes land between pixels) and the mock's optical size
 * not being applied. Both are handled here: the size is snapped to whole sp and
 * the family is picked from the size to get the matching baked optical instance.
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    // A fractional em lays glyph outlines on sub-pixel boundaries, which is what
    // reads as ragged. Snapping to whole sp keeps stems on the pixel grid.
    val raw = if (fontSize.isSp) fontSize.value else 14f
    val size = raw.roundToInt().toFloat()
    val family: FontFamily = rememberInterFor(size)
    val snapped = if (fontSize.isSp) size.sp else fontSize
    val snappedLine = if (lineHeight.isSp) lineHeight.value.roundToInt().sp else lineHeight
    M3Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = snapped,
        fontWeight = fontWeight ?: FontWeight(MOCK_WEIGHT),
        letterSpacing = (MOCK_SPACING_EM * size).sp,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        lineHeight = snappedLine,
        style = TextStyle.Default,
        fontFamily = family,
    )
}
