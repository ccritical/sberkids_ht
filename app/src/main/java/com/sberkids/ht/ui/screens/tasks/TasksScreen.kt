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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
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
import com.sberkids.ht.ui.theme.TasksMoreButton
import com.sberkids.ht.ui.theme.TasksMuted

@Composable
fun TasksScreen(modifier: Modifier = Modifier) {
    ScreenBackground(
        primary = BlobBlue,
        secondary = BlobPurple,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                MainTopBar(
                    actions = {
                        TopBarAction(
                            iconRes = R.drawable.ic_24_mono_plus,
                            contentDescription = stringResource(R.string.piggy_create),
                        )
                    },
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
            item { HeroBanner() }
            item { Spacer(Modifier.height(16.dp)) }
            item { ChecklistSection() }
        }
    }
}

/**
 * 328x232 banner (`1:12308`): white 328x192 surface, two flames clipped to the
 * side edges, and a 296x56 gradient CTA hanging 40dp below.
 */
@Composable
private fun HeroBanner() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface),
        ) {
            Column(modifier = Modifier.padding(start = 25.dp, top = 29.dp)) {
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

        // Flames sit in 56dp columns clipped by the banner, and they paint over
        // the CTA (measured: violet strokes inside the button area).
        Box(
            modifier = Modifier
                .padding(top = 112.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
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

        // Left column: x 0..56, flame 46.7x72.2 at y=121, overflowing below.
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(192.dp)
                .clipToBounds(),
        ) {
            Illustration(
                resId = R.drawable.tasks_flame_l,
                modifier = Modifier
                    .offset(x = (-2).dp, y = 122.dp)
                    .size(width = 46.7.dp, height = 72.2.dp),
            )
        }

        // Right column is pinned to the banner's right edge (x 272..328 in the
        // 328dp mock). Anchoring it with offset(x = 272.dp) broke as soon as the
        // banner stretched, so it is aligned to End instead.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(56.dp)
                .height(192.dp)
                .clipToBounds(),
        ) {
            Illustration(
                resId = R.drawable.tasks_flame_r,
                modifier = Modifier
                    .offset(x = 11.dp, y = 54.dp)
                    .size(width = 71.dp, height = 84.dp),
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

/**
 * Checklist block, measured on the 3x render of `1:12199`:
 *   white card       324x470 @ (18,374), r=18
 *   header           @ (41,401) 139x14
 *   inner row        280x80  @ (40,444), tint, r=20
 *   task cards       160x212 @ (40,536) and (212,536), tint, r=20
 *   "more" button    296x56  @ (32,772), #E4EBF0, r=20 — inside the white card
 * The white card is 18dp inset while its children start at 40dp, so the cards
 * bleed past the card's right edge and are clipped, as in the mock.
 */
@Composable
private fun ChecklistSection() {
    val cards = taskCards()
    Column(
        modifier = Modifier
            .padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .padding(top = 27.dp, bottom = 18.dp),
    ) {
        Text(
            text = stringResource(R.string.tasks_checklist_title),
            fontSize = 15.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier
                .padding(start = 23.dp)
                .width(139.dp),
        )

        Spacer(Modifier.height(29.dp))

        Row(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp)
                .fillMaxWidth()
                .height(80.dp)
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
                    .padding(start = 25.dp)
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

        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 22.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(cards) { card ->
                TaskCardView(card)
            }
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(TasksMoreButton),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.tasks_more),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            )
        }
    }
}

private data class TaskCard(
    val title: String,
    val image: Int,
    val artwork: Int? = null,
)

@Composable
private fun taskCards(): List<TaskCard> = listOf(
    TaskCard(
        title = stringResource(R.string.tasks_card_1_title),
        image = R.drawable.tasks_card_money,
        // Pre-composited blob + artwork + badge, matching the blurred oval that
        // sits behind the moneybox in the mock.
        artwork = R.drawable.tasks_card_money_art,
    ),
    TaskCard(
        title = stringResource(R.string.tasks_card_2_title),
        image = R.drawable.tasks_card_default,
    ),
)

@Composable
private fun TaskCardView(card: TaskCard) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(212.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TasksCardBg),
    ) {
        Text(
            text = card.title,
            fontSize = 16.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier
                .offset(x = 17.dp, y = 21.dp)
                .width(97.dp),
        )
        if (card.artwork != null) {
            // the composite covers the whole card below the title
            Illustration(
                resId = card.artwork,
                modifier = Modifier
                    .offset(x = 0.dp, y = 0.dp)
                    .size(width = 160.dp, height = 212.dp),
            )
        } else {
            Illustration(
                resId = card.image,
                modifier = Modifier
                    .offset(x = 16.dp, y = 76.dp)
                    .size(120.dp),
            )
        }
    }
}
