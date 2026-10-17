package com.sberkids.ht.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.sberkids.ht.ui.theme.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TertiaryText
import com.sberkids.ht.ui.theme.Track

/** White rounded surface used by every card in the mock. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    corner: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(Surface),
    ) {
        content()
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    track: Color = Track,
    fill: Color = Color(0xFF19D13B),
    height: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(track),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(fill),
        )
    }
}

@Composable
fun Footnote(text: String, modifier: Modifier = Modifier, color: Color = TertiaryText) {
    AppText(text, modifier = modifier, fontSize = 12.sp, lineHeight = 14.sp, color = color)
}

@Composable
fun LabelChip(text: String, modifier: Modifier = Modifier, background: Color = Track) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        AppText(text, fontSize = 11.sp, color = TertiaryText, maxLines = 1)
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(
            text = text,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            color = PrimaryText,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}
