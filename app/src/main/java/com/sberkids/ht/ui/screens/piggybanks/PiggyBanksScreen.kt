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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R
import com.sberkids.ht.ui.components.AnimatedPiggyIllustration
import com.sberkids.ht.ui.components.Illustration
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.ScreenBackground
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.theme.AccentBlue
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.AppText
import com.sberkids.ht.ui.theme.BlobPinkSoft
import com.sberkids.ht.ui.theme.BlobYellow
import com.sberkids.ht.ui.theme.ButtonGradient
import com.sberkids.ht.ui.theme.CtaGradient
import com.sberkids.ht.ui.theme.MutedIcon
import com.sberkids.ht.ui.theme.PiggyCardBg
import com.sberkids.ht.ui.theme.PiggyDoneGreen
import com.sberkids.ht.ui.theme.PiggyIconCircle
import com.sberkids.ht.ui.theme.PiggyIconCircleWarm
import com.sberkids.ht.ui.theme.PiggyProgressTrack
import com.sberkids.ht.ui.theme.PiggyWarnIcon
import com.sberkids.ht.ui.theme.PrimaryText
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                MainTopBar(
                    actions = {
                        TintedIcon(
                            resId = R.drawable.ic_24_mono_plus,
                            tint = AccentGreen,
                            size = 18.dp,
                        )
                    },
                )
            }
            item { TotalCard() }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AllPiggyRow()
                }
            }
            item { Spacer(Modifier.height(10.dp)) }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    cells.forEachIndexed { index, cell ->
                        PiggyCellCard(cell)
                        if (index < cells.lastIndex) Spacer(Modifier.height(16.dp))
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CreateButton()
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
            // "На что копят другие" is one white 328x500 card: heading at the
            // top, the card strip in the middle, the CTA pinned 24dp above the
            // bottom edge. The strip bleeds past the card and is clipped by it.
            item { OthersCard() }
            item { Spacer(Modifier.height(24.dp)) }
            item { ArchiveRow() }
        }
    }
}

/**
 * Balance block, measured on the mock: the artwork's clip is 140x176 at
 * (220,112), so the block itself is 360x176; the white sum card is 328x120 with
 * its top at y=144, i.e. 32dp below the block top and 24dp above its bottom.
 * The piggy and three sparkles animate independently inside the fixed clip.
 * An extra 40dp below the block fits the settled fragments (down to 211dp)
 * before the next card starts.
 */
@Composable
private fun TotalCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface),
        ) {
            Column(modifier = Modifier.padding(start = 25.dp, top = 39.7.dp)) {
                AppText(
                    text = stringResource(R.string.piggy_total_title),
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    color = PrimaryText,
                )
                Spacer(Modifier.height(12.dp))
                AppText(
                    text = stringResource(R.string.piggy_total_caption),
                    fontSize = 11.sp,
                    color = TertiaryText,
                )
            }
        }
        AnimatedPiggyIllustration(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 140.dp, height = 176.dp),
        )
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
            AppText(
                text = stringResource(R.string.piggy_total_footnote),
                fontSize = 13.sp,
                color = PrimaryText,
                modifier = Modifier.weight(1f),
            )
            AppText(
                text = stringResource(R.string.piggy_total_percent),
                fontSize = 15.sp,
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
                AppText(
                    text = cell.caption,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
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
                    AppText(
                        text = cell.ctaLabel.orEmpty(),
                        fontSize = 10.sp,
                        lineHeight = 11.sp,
                        color = PrimaryText,
                    )
                }
            } else {
                Spacer(Modifier.height(37.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    AppText(
                        text = cell.amount,
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        color = PrimaryText,
                    )
                    if (cell.goal.isNotEmpty()) {
                        Spacer(Modifier.width(2.dp))
                        AppText(
                            text = stringResource(R.string.piggy_currency),
                            fontSize = 11.sp,
                            color = TertiaryText,
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        AppText(
                            text = cell.goal,
                            fontSize = 11.sp,
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

                AppText(
                    text = cell.caption,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
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
            AppText(
                text = stringResource(R.string.piggy_create),
                fontSize = 14.sp,
                color = PrimaryText,
            )
        }
    }
}


/**
 * "На что копят другие" block, measured on the mock: a white 328x500 card at
 * r=20 holding a 215x18 heading at (26,29), a 704dp-wide card strip starting at
 * x=24 and y=72 (clipped by the card), and a 280x56 gradient CTA at (24,420).
 */
@Composable
private fun OthersCard() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(500.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        AppText(
            text = stringResource(R.string.piggy_others_title),
            fontSize = 16.sp,
            lineHeight = 18.sp,
            color = PrimaryText,
            modifier = Modifier.padding(start = 26.dp, top = 29.dp),
        )
        Spacer(Modifier.height(25.dp))
        Carousel()
        Spacer(Modifier.weight(1f))
        SeeAllButton()
        Spacer(Modifier.height(24.dp))
    }
}

/** 280x56 gradient CTA pinned to the bottom of the block. */
@Composable
private fun SeeAllButton() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(CtaGradient)),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = stringResource(R.string.piggy_see_all),
            fontSize = 14.sp,
            color = PrimaryText,
        )
    }
}

private data class CarouselCard(
    val title: String,
    val amount: String,
)

/** 224x324 cards, 16dp apart, bleeding past the card edge as in the mock. */
@Composable
private fun Carousel() {
    val cards = listOf(
        CarouselCard(
            stringResource(R.string.piggy_carousel_1_title),
            stringResource(R.string.piggy_carousel_1_amount),
        ),
        CarouselCard(
            stringResource(R.string.piggy_carousel_2_title),
            stringResource(R.string.piggy_carousel_2_amount),
        ),
        CarouselCard(
            stringResource(R.string.piggy_carousel_3_title),
            stringResource(R.string.piggy_carousel_3_amount),
        ),
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 16.dp),
    ) {
        items(cards) { card ->
            Box(
                modifier = Modifier
                    .width(224.dp)
                    .height(324.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PiggyCardBg),
            ) {
                Illustration(
                    resId = R.drawable.img_piggy_carousel,
                    modifier = Modifier
                        .offset(x = 24.dp, y = 24.dp)
                        .size(176.dp),
                )
                AppText(
                    text = card.title,
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
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
                        AppText(
                            text = card.amount,
                            fontSize = 15.sp,
                            color = PrimaryText,
                        )
                        Spacer(Modifier.width(2.dp))
                        AppText(
                            text = stringResource(R.string.piggy_currency),
                            fontSize = 12.sp,
                            color = TertiaryText,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AccentBlue),
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
                // Figma paints this icon #A3C2DB, not the green used elsewhere.
                tint = MutedIcon,
                size = 20.dp,
            )
            Spacer(Modifier.width(14.dp))
            AppText(
                text = stringResource(R.string.piggy_archive),
                fontSize = 13.sp,
                color = PrimaryText,
                modifier = Modifier.weight(1f),
            )
            AppText(
                text = stringResource(R.string.piggy_count),
                fontSize = 12.sp,
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
