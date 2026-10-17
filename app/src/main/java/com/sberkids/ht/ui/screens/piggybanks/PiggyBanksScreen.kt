package com.sberkids.ht.ui.screens.piggybanks

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
import androidx.compose.foundation.shape.CircleShape
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
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.AccentGreenText
import com.sberkids.ht.ui.theme.BlobPinkSoft
import com.sberkids.ht.ui.theme.BlobYellow
import com.sberkids.ht.ui.theme.ButtonGradient
import com.sberkids.ht.ui.theme.CtaGradient
import com.sberkids.ht.ui.theme.PiggyCardBg
import com.sberkids.ht.ui.theme.PiggyDoneGreen
import com.sberkids.ht.ui.theme.PiggyIconCircle
import com.sberkids.ht.ui.theme.PiggyIconCircleWarm
import com.sberkids.ht.ui.theme.PiggyWarnIcon
import com.sberkids.ht.ui.theme.PiggyProgressTrack
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.SecondaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TertiaryText

private data class PiggyCell(
    val amount: String,
    val caption: String,
    val goal: String,
    val progress: Float,
    /** Artwork inside the 104dp cell; null when the mock shows an icon instead. */
    val image: Int? = null,
    val showProgress: Boolean = true,
    val isDone: Boolean = false,
    val ctaLabel: String? = null,
    /** Circle colour behind the icon when [image] is null. */
    val iconCircle: Color? = null,
    val iconRes: Int = R.drawable.ic_24_mono_piggy,
)

@Composable
fun PiggyBanksScreen(modifier: Modifier = Modifier) {
    ScreenBackground(
        primary = BlobPinkSoft,
        secondary = BlobYellow,
        modifier = modifier,
    ) {
        val cells = piggyCells()
        Column(modifier = Modifier.fillMaxSize()) {
            MainTopBar(
                actions = {
                    TintedIcon(
                        resId = R.drawable.ic_24_mono_plus,
                        tint = AccentGreen,
                        size = 18.dp,
                    )
                },
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            ) {
                item { TotalCard() }
                item { Spacer(Modifier.height(10.dp)) }
                item { AllPiggyRow() }
                item { Spacer(Modifier.height(10.dp)) }
                item {
                    cells.forEachIndexed { index, cell ->
                        PiggyCellCard(cell)
                        if (index < cells.lastIndex) Spacer(Modifier.height(16.dp))
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
                item { CreateButton() }
                item { Spacer(Modifier.height(24.dp)) }
                item { CarouselTitle() }
                item { Spacer(Modifier.height(12.dp)) }
                item { Carousel() }
                item { Spacer(Modifier.height(24.dp)) }
                item { ArchiveRow() }
            }
        }
    }
}

/** 328x120 white card: total on the left, big piggy artwork clipped on the right. */
@Composable
private fun TotalCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Illustration(
            resId = R.drawable.img_piggy_hero,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 8.dp)
                .size(width = 172.dp, height = 172.dp),
        )
        Column(
            modifier = Modifier
                .padding(start = 25.dp, top = 40.dp),
        ) {
            Text(
                text = stringResource(R.string.piggy_total_title),
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.piggy_total_caption),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TertiaryText,
            )
        }
    }
}

/** 328x72 white strip: "Годовой процент" on the left, "11%" on the right. */
@Composable
private fun AllPiggyRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.piggy_total_footnote),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.piggy_total_percent),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            )
        }
    }
}

/**
 * CellMoneyBox, 328x128, r=20, white.
 *
 * Figma keeps a green `#19D13B` background behind the artwork in every cell but
 * with `opacity: 0`, so the rendered card is plain white — reproducing it green
 * was the main mistake. Layout per cell:
 *   artwork 104x104 at (12,12) with the 80x80 image at (24,24)
 *   text column starts at x=120
 *   when there is a progress bar: amount row at y=37, bar at y=64, caption below
 *   the "favourite"/"starter" cells instead put the caption under the amount
 */
@Composable
private fun PiggyCellCard(cell: PiggyCell) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        if (cell.isDone) {
            // Only the completed cell keeps the green band visible in Figma.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PiggyDoneGreen),
            )
        }
        if (cell.image != null) {
            Illustration(
                resId = cell.image,
                modifier = Modifier
                    .offset(x = 24.dp, y = 24.dp)
                    .size(80.dp),
            )
        } else {
            Box(
                modifier = Modifier
                    .offset(x = 24.dp, y = 24.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(cell.iconCircle ?: PiggyIconCircle),
                contentAlignment = Alignment.Center,
            ) {
                TintedIcon(
                    resId = cell.iconRes,
                    tint = if (cell.iconRes == R.drawable.ic_24_mono_piggy_error) {
                        PiggyWarnIcon
                    } else {
                        AccentGreen
                    },
                    size = 24.dp,
                )
            }
        }

        Column(
            modifier = Modifier.padding(start = 120.dp, end = 24.dp),
        ) {
            if (cell.isDone) {
                // Completed cell: white two-line caption at y=33, then a 130x24
                // gradient button at y=76. No amount, no progress bar.
                Spacer(Modifier.height(33.dp))
                Text(
                    text = cell.caption,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.width(142.dp),
                )
                Spacer(Modifier.height(9.dp))
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(*ButtonGradient)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = cell.ctaLabel.orEmpty(),
                        fontSize = 10.sp,
                        lineHeight = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                    )
                }
            } else {
                Spacer(Modifier.height(37.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = cell.amount,
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText,
                    )
                    if (cell.goal.isNotEmpty()) {
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = stringResource(R.string.piggy_currency),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TertiaryText,
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = cell.goal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TertiaryText,
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
                    }
                }

                if (cell.showProgress) {
                    Spacer(Modifier.height(13.dp))
                    ProgressBar(progress = cell.progress)
                    Spacer(Modifier.height(6.dp))
                } else {
                    Spacer(Modifier.height(8.dp))
                }

                Text(
                    text = cell.caption,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(PiggyProgressTrack),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
                .background(AccentGreen),
        )
    }
}

/** 328x56 gradient CTA with a leading plus icon. */
@Composable
private fun CreateButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(CtaGradient)),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TintedIcon(
                resId = R.drawable.ic_16_mono_plus,
                tint = PrimaryText,
                size = 18.dp,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.piggy_create),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText,
            )
        }
    }
}

@Composable
private fun CarouselTitle() {
    Text(
        text = stringResource(R.string.piggy_others_title),
        fontSize = 16.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryText,
        modifier = Modifier.padding(start = 2.dp),
    )
}

private data class CarouselCard(
    val title: String,
    val amount: String,
    val image: Int,
)

/** Three 224x324 cards, 16dp apart; the third peeks in from the right edge. */
@Composable
private fun Carousel() {
    val cards = listOf(
        CarouselCard(
            stringResource(R.string.piggy_carousel_1_title),
            stringResource(R.string.piggy_carousel_1_amount),
            R.drawable.img_piggy_1,
        ),
        CarouselCard(
            stringResource(R.string.piggy_carousel_2_title),
            stringResource(R.string.piggy_carousel_2_amount),
            R.drawable.img_piggy_2,
        ),
        CarouselCard(
            stringResource(R.string.piggy_carousel_3_title),
            stringResource(R.string.piggy_carousel_3_amount),
            R.drawable.img_piggy_3,
        ),
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(end = 16.dp),
    ) {
        items(cards) { card ->
            Box(
                modifier = Modifier
                    .width(224.dp)
                    .height(324.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PiggyCardBg),
            ) {
                Illustration(
                    resId = card.image,
                    modifier = Modifier
                        .offset(x = 24.dp, y = 24.dp)
                        .size(176.dp),
                )
                Text(
                    text = card.title,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 2,
                    modifier = Modifier
                        .offset(x = 25.dp, y = 219.dp)
                        .width(174.dp),
                )
                Row(
                    modifier = Modifier
                        .offset(x = 25.dp, y = 279.dp)
                        .width(175.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = card.amount,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryText,
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = stringResource(R.string.piggy_currency),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TertiaryText,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(com.sberkids.ht.ui.theme.AccentBlue),
                        contentAlignment = Alignment.Center,
                    ) {
                        TintedIcon(
                            resId = R.drawable.ic_16_mono_plus,
                            tint = Color.White,
                            size = 12.dp,
                        )
                    }
                }
            }
        }
    }
}

/** 328x72 archive strip at the bottom of the screen. */
@Composable
private fun ArchiveRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TintedIcon(
                resId = R.drawable.ic_24_mono_arhive,
                tint = AccentGreen,
                size = 18.dp,
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = stringResource(R.string.piggy_archive),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.piggy_count),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TertiaryText,
            )
        }
    }
}

@Composable
private fun piggyCells(): List<PiggyCell> = listOf(
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_favourite_amount),
        caption = stringResource(R.string.piggy_cell_favourite),
        goal = "",
        progress = 0f,
        image = null,
        showProgress = false,
        iconCircle = PiggyIconCircleWarm,
        iconRes = R.drawable.ic_24_mono_piggy_error,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_starter_amount),
        caption = stringResource(R.string.piggy_cell_starter),
        goal = "",
        progress = 0f,
        image = null,
        showProgress = false,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_dream_amount),
        caption = stringResource(R.string.piggy_cell_dream),
        goal = stringResource(R.string.piggy_cell_dream_goal),
        progress = 0.40f,
        image = R.drawable.piggy_art_3,
    ),
    PiggyCell(
        amount = "",
        caption = stringResource(R.string.piggy_cell_done),
        goal = "",
        progress = 0f,
        image = R.drawable.piggy_art_4,
        showProgress = false,
        isDone = true,
        ctaLabel = stringResource(R.string.piggy_cell_done_cta),
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_shoes_amount),
        caption = stringResource(R.string.piggy_cell_shoes),
        goal = stringResource(R.string.piggy_cell_shoes_goal),
        progress = 0.90f,
        image = R.drawable.piggy_art_5,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_slime_amount),
        caption = stringResource(R.string.piggy_cell_slime),
        goal = stringResource(R.string.piggy_cell_slime_goal),
        progress = 0.83f,
        image = R.drawable.piggy_art_6,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_markers_amount),
        caption = stringResource(R.string.piggy_cell_markers),
        goal = stringResource(R.string.piggy_cell_markers_goal),
        progress = 0.50f,
        image = R.drawable.piggy_art_7,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_band_amount),
        caption = stringResource(R.string.piggy_cell_band),
        goal = stringResource(R.string.piggy_cell_band_goal),
        progress = 0.90f,
        image = R.drawable.piggy_art_8,
    ),
    PiggyCell(
        amount = stringResource(R.string.piggy_cell_robot_amount),
        caption = stringResource(R.string.piggy_cell_robot),
        goal = stringResource(R.string.piggy_cell_robot_goal),
        progress = 0.33f,
        image = R.drawable.piggy_art_9,
    ),
)
