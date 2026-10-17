package com.sberkids.ht.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R
import com.sberkids.ht.ui.components.Illustration
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.ScreenBackground
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.components.TopBarAction
import com.sberkids.ht.ui.theme.AccentBlue
import com.sberkids.ht.ui.theme.BlobBlue
import com.sberkids.ht.ui.theme.BlobPurple
import com.sberkids.ht.ui.theme.ButtonGradient
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TasksCardBg
import com.sberkids.ht.ui.theme.TasksMuted

@Composable
fun TasksScreen(modifier: Modifier = Modifier) {
    ScreenBackground(
        primary = BlobBlue,
        secondary = BlobPurple,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainTopBar(
                actions = {
                    TopBarAction(
                        iconRes = R.drawable.ic_24_mono_plus,
                        contentDescription = stringResource(R.string.piggy_create),
                    )
                },
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item { HeroBanner() }
                item { Spacer(Modifier.height(16.dp)) }
                item { ChecklistSection() }
                item { Spacer(Modifier.height(40.dp)) }
                item { MoreButton() }
            }
        }
    }
}

/**
 * 328x232 banner (node `1:12308`): white 328x192 surface with two flame
 * decorations clipped to the side edges, plus a 296x56 gradient CTA that hangs
 * 40dp below the surface.
 */
@Composable
private fun HeroBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface),
        ) {
            Illustration(
                resId = R.drawable.tasks_flame_l,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-2).dp, y = 64.dp)
                    .size(width = 52.dp, height = 76.dp),
            )
            Illustration(
                resId = R.drawable.tasks_flame_r,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = 54.dp)
                    .size(width = 76.dp, height = 88.dp),
            )
            Column(
                modifier = Modifier.padding(start = 25.dp, top = 29.dp, end = 25.dp),
            ) {
                Text(
                    text = stringResource(R.string.tasks_banner_title),
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                )
                Spacer(Modifier.height(9.dp))
                Text(
                    text = stringResource(R.string.tasks_banner_text),
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TasksMuted,
                    modifier = Modifier.width(221.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 112.dp, start = 16.dp)
                .width(296.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(*ButtonGradient)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.tasks_banner_cta),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            )
        }
    }
}

/**
 * The "Чем заняться" block is one 324dp-wide white card (x=18..341, r=20) that
 * holds the section header, an inner 328x80 tinted row and the two task cards.
 * Rendering these as separate siblings is what broke the layout before.
 */
@Composable
private fun ChecklistSection() {
    val cards = taskCards()
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .padding(top = 22.dp, bottom = 22.dp),
    ) {
        Text(
            text = stringResource(R.string.tasks_checklist_title),
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier.padding(start = 23.dp, bottom = 12.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(TasksCardBg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.tasks_checklist_row),
                fontSize = 15.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier
                    .padding(start = 21.dp)
                    .width(76.dp),
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(end = 18.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentBlue),
                contentAlignment = Alignment.Center,
            ) {
                TintedIcon(
                    resId = R.drawable.ic_16_mono_arrow_right,
                    tint = Color.White,
                    size = 14.dp,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp),
        ) {
            items(cards) { card ->
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(212.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(TasksCardBg),
                ) {
                    Text(
                        text = card.title,
                        fontSize = 15.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        modifier = Modifier
                            .offset(x = 17.dp, y = 21.dp)
                            .width(97.dp),
                    )
                    Illustration(
                        resId = card.image,
                        modifier = Modifier
                            .offset(x = 16.dp, y = 76.dp)
                            .size(120.dp),
                    )
                }
            }
        }
    }
}

private data class TaskCard(
    val title: String,
    val image: Int,
)

@Composable
private fun taskCards(): List<TaskCard> = listOf(
    TaskCard(
        stringResource(R.string.tasks_card_1_title),
        R.drawable.tasks_card_money,
    ),
    TaskCard(
        stringResource(R.string.tasks_card_2_title),
        R.drawable.tasks_card_default,
    ),
)

/** 296x56 light button below the white card. */
@Composable
private fun MoreButton() {
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TasksCardBg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.tasks_more),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
        )
    }
}
