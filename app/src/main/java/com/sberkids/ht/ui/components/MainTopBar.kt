package com.sberkids.ht.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sberkids.ht.R
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.Surface

/**
 * Top bar shared by the four tabs: avatar and logo on the left, optional
 * action buttons on the right.
 */
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)? = null,
    height: Dp = 112.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Illustration(
                resId = R.drawable.img_avatar,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )
            Spacer(Modifier.width(8.dp))
            Illustration(
                resId = R.drawable.img_logo,
                modifier = Modifier
                    .width(126.dp)
                    .height(34.dp),
            )
            Spacer(Modifier.weight(1f))
            actions?.invoke(this)
        }
    }
}

@Composable
fun TopBarAction(
    iconRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(modifier = modifier.size(62.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            TintedIcon(
                resId = iconRes,
                tint = PrimaryText,
                contentDescription = contentDescription,
                size = 18.dp,
            )
        }
    }
}
