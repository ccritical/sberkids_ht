package com.sberkids.ht.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sberkids.ht.ui.theme.AppBackground

/**
 * Soft two-blob backdrop used by three of the four tabs. The fourth (home)
 * paints three blobs at different offsets — see HomeBackground.
 */
@Composable
fun ScreenBackground(
    primary: Color,
    secondary: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(AppBackground)) {
        Blob(
            color = primary,
            size = 414.dp,
            modifier = Modifier.offset(x = 121.dp, y = (-335).dp),
        )
        Blob(
            color = secondary,
            size = 320.dp,
            modifier = Modifier.offset(x = (-160).dp, y = (-160).dp),
        )
        content()
    }
}

/** Home tab: yellow blob centred plus two lime blobs at the left/right edges. */
@Composable
fun HomeBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxSize().background(AppBackground)) {
        Blob(
            color = Color(0xFFE5FF43),
            size = 380.dp,
            modifier = Modifier.offset(x = 5.dp, y = 84.dp),
        )
        Blob(
            color = Color(0xFF87FF74),
            size = 300.dp,
            modifier = Modifier.offset(x = (-179).dp, y = 44.dp),
        )
        Blob(
            color = Color(0xFF87FF74),
            size = 300.dp,
            modifier = Modifier.offset(x = 269.dp, y = 204.dp),
        )
        content()
    }
}

@Composable
private fun Blob(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0f)),
                ),
            ),
    )
}
