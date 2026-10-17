package com.sberkids.ht.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TintedIcon(
    resId: Int,
    tint: Color,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    Icon(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size),
    )
}

@Composable
fun VectorIcon(
    icon: ImageVector,
    tint: Color,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size),
    )
}

/** Round icon badge: coloured 48dp circle with a 20dp glyph inside. */
@Composable
fun IconBadge(
    iconRes: Int,
    background: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 48.dp,
    iconSize: Dp = 20.dp,
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        TintedIcon(resId = iconRes, tint = tint, size = iconSize)
    }
}

@Composable
fun RoundedIconBadge(
    iconRes: Int,
    background: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 48.dp,
    corner: Dp = 16.dp,
    iconSize: Dp = 20.dp,
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(corner))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        TintedIcon(resId = iconRes, tint = tint, size = iconSize)
    }
}

@Composable
fun Illustration(
    resId: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Icon(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color.Unspecified,
    )
}
