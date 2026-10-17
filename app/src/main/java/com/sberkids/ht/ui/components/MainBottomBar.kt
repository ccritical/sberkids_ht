package com.sberkids.ht.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R
import com.sberkids.ht.ui.navigation.MainTab
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.BottomBarBottom
import com.sberkids.ht.ui.theme.BottomBarTop
import com.sberkids.ht.ui.theme.DoodlePink
import com.sberkids.ht.ui.theme.MutedIcon
import com.sberkids.ht.ui.theme.Surface

private val barHeight = 88.dp
private val tabWidth = 60.dp
private val assistantSize = 48.dp

/**
 * Bottom navigation from the mock: a white pill with four labelled tabs and a
 * raised assistant button in the middle.
 */
@Composable
fun MainBottomBar(
    selected: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onAssistantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = MainTab.entries
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(BottomBarTop, BottomBarBottom)))
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 16.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TabSlot(tabs[0], selected, onTabSelected)
            TabSlot(tabs[1], selected, onTabSelected)
            AssistantSlot(onAssistantClick)
            TabSlot(tabs[2], selected, onTabSelected)
            TabSlot(tabs[3], selected, onTabSelected)
        }
    }
}

@Composable
private fun TabSlot(
    tab: MainTab,
    selected: MainTab,
    onTabSelected: (MainTab) -> Unit,
) {
    val isSelected = tab == selected
    val iconOffset by animateDpAsState(
        targetValue = if (isSelected) (-4).dp else 0.dp,
        animationSpec = tween(180),
        label = "tabLift",
    )
    Column(
        modifier = Modifier
            .size(width = tabWidth, height = 72.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onTabSelected(tab) }
            .semantics { this.selected = isSelected },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TintedIcon(
            resId = tab.iconRes,
            tint = if (isSelected) AccentGreen else MutedIcon,
            contentDescription = null,
            size = 20.dp,
            modifier = Modifier.offset(y = iconOffset),
        )
        Text(
            text = stringResource(tab.titleRes),
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) AccentGreen else MutedIcon,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun AssistantSlot(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(assistantSize)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(DoodlePink, AccentGreen)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Illustration(
            resId = R.drawable.img_assistant,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
    }
}
