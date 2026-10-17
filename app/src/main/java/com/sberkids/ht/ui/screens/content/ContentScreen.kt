package com.sberkids.ht.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.sberkids.ht.ui.theme.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R
import com.sberkids.ht.ui.components.Illustration
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.ScreenBackground
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.AccentPurple
import com.sberkids.ht.ui.theme.BlobPurple
import com.sberkids.ht.ui.theme.BlobPurpleLight
import com.sberkids.ht.ui.theme.ContentProgressTrack
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TertiaryText
import com.sberkids.ht.ui.theme.ToneContent

/**
 * Content tab, reproduced from Figma `1:11331`.
 *
 * Block order and the gaps between them, measured on the frame (NavBar is 112
 * high, everything below is absolute):
 *
 *   hero carousel   112..408   (touches the bar, no gap)
 *   story thumbs    424..536   (+16)
 *   stream player   572..712   (+36)
 *   chips           760..816   (+48)
 *   card 1          848..1144  (+32)
 *   card 2         1168..1446  (+24)
 *   info banner    1470..1574  (+24)
 *   top-5 header   1606..1668  (+32)
 *   top-5 cards    1684..1946  (+16)
 *   card 3         1994..2290  (+48)
 *   algoritmika    2338..2710  (+48)  pink block, logo overlaps its top
 *   card 4         2742..3020  (+32)
 *   card 5         3044..3326  (+24)
 */
@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    ScreenBackground(
        primary = BlobPurple,
        secondary = BlobPurpleLight,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp),
        ) {
            item { MainTopBar() }

            item { HeroCarousel() }

            item { Spacer(Modifier.height(16.dp)) }
            item { StoryCarousel() }

            item { Spacer(Modifier.height(36.dp)) }
            item { StreamPlayer() }

            item { Spacer(Modifier.height(40.dp)) }
            item { CategoriesRow() }

            item { Spacer(Modifier.height(24.dp)) }
            item { ArticleCard(image = R.drawable.img_content_card_1, tall = true, bar = true) }

            item { Spacer(Modifier.height(24.dp)) }
            item { ArticleCard(image = R.drawable.img_content_card_2, seen = true) }

            item { Spacer(Modifier.height(24.dp)) }
            item { InfoBanner() }

            item { Spacer(Modifier.height(32.dp)) }
            item { SectionTitle(stringResource(R.string.content_top5_header)) }
            item { Spacer(Modifier.height(16.dp)) }
            item { SmallCardRow() }

            item { Spacer(Modifier.height(48.dp)) }
            item { ArticleCard(image = R.drawable.img_content_card_3, tall = true) }

            item { Spacer(Modifier.height(24.dp)) }
            item { AlgoritmikaBlock() }

            item { Spacer(Modifier.height(32.dp)) }
            item { ArticleCard(image = R.drawable.img_content_card_4, bar = true) }

            item { Spacer(Modifier.height(24.dp)) }
            item { PlaceholderCard() }
        }
    }
}

/**
 * 296x256 cards on a 312 step. The active card is the second one, so its left
 * neighbour shows 16dp at x=0 and the next card starts at x=328.
 */
@Composable
private fun HeroCarousel() {
    val cards = listOf(
        R.drawable.img_content_hero_prev,
        R.drawable.img_content_hero_story,
        R.drawable.img_content_hero_next,
        R.drawable.img_content_story_3,
    )
    LazyRow(
        state = rememberLazyListState(initialFirstVisibleItemIndex = 1),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
    ) {
        items(cards) { image ->
            Box(
                modifier = Modifier
                    .size(width = 296.dp, height = 256.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                // The CardTop group is exported with its scrim, tag and title
                // baked in, so no text is drawn on top of it.
                Illustration(
                    resId = image,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

private val stories = listOf(
    R.drawable.img_content_story_1,
    R.drawable.img_content_story_2,
    R.drawable.img_content_story_3,
    R.drawable.img_content_story_4,
    R.drawable.img_content_story_5,
)

/** 80x100 thumbnails 12dp apart with a 24dp play tag at the bottom. */
@Composable
private fun StoryCarousel() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(stories) { image ->
            Box(modifier = Modifier.size(width = 80.dp, height = 112.dp)) {
                Illustration(
                    resId = image,
                    modifier = Modifier
                        .size(width = 80.dp, height = 100.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Surface),
                    contentAlignment = Alignment.Center,
                ) {
                    TintedIcon(
                        resId = R.drawable.ic_16_mono_play,
                        tint = Color(0xFF52BAFF),
                        size = 12.dp,
                    )
                }
            }
        }
    }
}

/** 328x140 player: 100dp cover, grey kicker over a bold title, 40dp play button. */
@Composable
private fun StreamPlayer() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Illustration(
            resId = R.drawable.img_content_player_cover,
            modifier = Modifier
                .padding(start = 20.dp, top = 20.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .offset(x = 136.dp, y = 29.dp)
                .width(130.dp),
        ) {
            AppText(
                text = stringResource(R.string.content_stream_subtitle),
                fontSize = 13.sp,
                color = Color(0xFF7D868C),
                maxLines = 1,
            )
            Spacer(Modifier.height(8.dp))
            AppText(
                text = stringResource(R.string.content_stream_title),
                fontSize = 15.sp,
                lineHeight = 18.sp,
                color = PrimaryText,
                maxLines = 2,
            )
        }
        // Playback line: 118x4 at (155,99) inside the 140-tall card.
        Box(
            modifier = Modifier
                .offset(x = 139.dp, y = 99.dp)
                .width(118.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AccentPurple),
        )
        Box(
            modifier = Modifier
                .offset(x = 268.dp, y = 80.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE4EBF0)),
            contentAlignment = Alignment.Center,
        ) {
            TintedIcon(R.drawable.ic_16_mono_play, PrimaryText, size = 14.dp)
        }
    }
}

/** 56dp pills; the selected one is 2dp larger and carries a black 2dp ring. */
@Composable
private fun CategoriesRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            ChipPill(
                label = stringResource(R.string.content_chip_all),
                icon = R.drawable.ic_24_mono_piggy,
                tint = Color(0xFFB3BBDC),
                selected = true,
            )
        }
        item { ChipPill(stringResource(R.string.content_chip_ai), R.drawable.ic_24_mono_star_four, Color(0xFF8BB2FC)) }
        item { ChipPill(stringResource(R.string.content_chip_money), R.drawable.ic_24_mono_piggy, AccentGreen) }
        item { ChipPill(stringResource(R.string.content_chip_tales), R.drawable.ic_24_mono_palette, Color(0xFFFF8E00)) }
    }
}

@Composable
private fun ChipPill(label: String, icon: Int, tint: Color, selected: Boolean = false) {
    val ring = if (selected) 2.dp else 0.dp
    Row(
        modifier = Modifier
            .height(56.dp + ring * 2)
            .clip(RoundedCornerShape(20.dp + ring))
            .background(Surface)
            .then(if (selected) Modifier.border(ring, PrimaryText, RoundedCornerShape(22.dp)) else Modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TintedIcon(resId = icon, tint = tint, size = 20.dp)
        Spacer(Modifier.width(8.dp))
        AppText(label, fontSize = 13.sp, color = PrimaryText)
    }
}

/**
 * Article card, reproduced at the mock's 328dp reference width. The whole card
 * is scaled by its measured width, so the photo, text, bar and paddings keep the
 * mock's proportions on any screen instead of drifting apart.
 */
@Composable
private fun ArticleCard(
    image: Int,
    tall: Boolean = false,
    seen: Boolean = false,
    bar: Boolean = false,
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        val k = (with(density) { maxWidth.toPx() } / with(density) { 328.dp.toPx() }).toFloat()
        val h = if (tall) 296.dp else 278.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(h * k)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface),
        ) {
            Illustration(
                resId = image,
                modifier = Modifier
                    .padding(start = 16.dp * k, end = 16.dp * k, top = 16.dp * k)
                    .fillMaxWidth()
                    .height(164.dp * k)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )
            TagRow(y = 191.dp * k, scale = k)
            AppText(
                text = stringResource(
                    if (tall) R.string.content_card_long else R.string.content_card_short,
                ),
                fontSize = (15 * k).sp,
                lineHeight = (20 * k).sp,
                color = PrimaryText,
                maxLines = 2,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .offset(x = 17.dp * k, y = 211.dp * k)
                    .padding(end = 17.dp * k)
                    .height(40.dp * k)
                    .fillMaxWidth(),
            )
            LikesRow(y = (if (tall) 267.dp else 249.dp) * k, scale = k)
            if (bar) {
                ProgressBar(
                    progress = 0.76f,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 21.dp),
                )
            }
            if (seen) {
                SeenMark(x = 288.dp * k, y = 242.dp * k, scale = k)
            }
        }
    }
}

@Composable
private fun TagRow(y: androidx.compose.ui.unit.Dp, scale: Float = 1f) {
    Row(
        modifier = Modifier.offset(x = 17.dp * scale, y = y),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_16_mono_play),
            contentDescription = null,
            tint = TertiaryText,
            modifier = Modifier.size(width = 10.dp * scale, height = 10.8.dp * scale),
        )
        Spacer(Modifier.width(9.3.dp * scale))
        AppText(
            text = stringResource(R.string.content_tag_video),
            fontSize = (11 * scale).sp,
            lineHeight = (13 * scale).sp,
            color = TertiaryText,
            modifier = Modifier.offset(y = (-2 * scale).dp),
        )
    }
}

/** Heart at x=18, counter at x=36.6: a 6.6dp gap, 11sp regular. */
@Composable
private fun LikesRow(y: androidx.compose.ui.unit.Dp, scale: Float = 1f) {
    // Figma Y points at the ink, not the line box. The heart png has no padding,
    // so it sits at the row top; the 11sp/13sp text paints its ink 2.5dp lower,
    // hence the -1.5dp nudge to line up with the mock's 1dp difference.
    Row(
        modifier = Modifier.offset(x = 18.dp * scale, y = y),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_24_mono_heart),
            contentDescription = null,
            tint = TertiaryText,
            modifier = Modifier.size(width = 12.dp * scale, height = 10.7.dp * scale),
        )
        Spacer(Modifier.width(6.6.dp * scale))
        AppText(
            text = stringResource(R.string.content_likes),
            fontSize = (11 * scale).sp,
            lineHeight = (13 * scale).sp,
            color = TertiaryText,
            modifier = Modifier.offset(y = (-2 * scale).dp),
        )
    }
}

@Composable
private fun SeenMark(x: androidx.compose.ui.unit.Dp, y: androidx.compose.ui.unit.Dp, scale: Float = 1f) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(24.dp * scale)
            .clip(CircleShape)
            .background(AccentGreen),
        contentAlignment = Alignment.Center,
    ) {
        TintedIcon(R.drawable.ic_16_mono_done, Color.White, size = 12.dp * scale)
    }
}

/** 80x6 track with a violet fill. */
@Composable
private fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(ContentProgressTrack),
    ) {
        Box(
            modifier = Modifier
                .width(80.dp * progress)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AccentPurple),
        )
    }
}
/** 328x104 banner; the artwork is baked into the exported group. */
@Composable
private fun InfoBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0A3199), Color(0xFF010510)),
                    ),
                ),
        )
        // 114x140 artwork hangs 18dp above the card's top edge and is clipped.
        Illustration(
            resId = R.drawable.img_content_info_art,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-18).dp)
                .size(width = 114.dp, height = 140.dp),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(start = 24.dp, top = 29.dp)) {
            AppText(
                text = stringResource(R.string.content_banner_title),
                fontSize = 16.sp,
                color = Color.White,
            )
            Spacer(Modifier.height(8.dp))
            AppText(
                text = stringResource(R.string.content_banner_text),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.width(162.dp),
            )
        }
        // Ad label: a 145x20 white pill at (183,84) inside the banner.
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 20.dp)
                .width(145.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(9.dp))
            AppText(
                text = stringResource(R.string.content_ad),
                fontSize = 9.sp,
                color = PrimaryText,
                maxLines = 1,
            )
            Spacer(Modifier.weight(1f))
            TintedIcon(
                resId = R.drawable.ic_12_mono_info_circle,
                tint = PrimaryText,
                size = 10.dp,
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    AppText(
        text = text,
        fontSize = 20.sp,
        lineHeight = 22.sp,
        color = PrimaryText,
        modifier = Modifier.padding(start = 16.dp),
    )
}

private val topRowCards = listOf(
    R.drawable.img_top5_2 to false,
    R.drawable.img_top5_1 to true,
)

private val pinkRowCards = listOf(
    R.drawable.img_top5_1 to false,
    R.drawable.img_top5_2 to true,
)

/** 180x262 cards 24dp apart (mock: x=16 and x=220). */
@Composable
private fun SmallCardRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(topRowCards) { pair -> SmallCard(pair.first, pair.second) }
    }
}

@Composable
private fun SmallCard(image: Int, seen: Boolean) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(262.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Illustration(
            resId = image,
            modifier = Modifier
                .offset(x = 16.dp, y = 16.dp)
                .width(148.dp)
                .height(104.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier.offset(x = 17.dp, y = 131.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TintedIcon(R.drawable.ic_16_mono_play, TertiaryText, size = 10.dp)
            Spacer(Modifier.width(6.dp))
            AppText(
                text = stringResource(R.string.content_tag_video),
                fontSize = 11.sp,
                color = TertiaryText,
            )
        }
        AppText(
            text = stringResource(R.string.content_card_long),
            fontSize = 13.sp,
            lineHeight = 15.sp,
            color = PrimaryText,
            maxLines = 2,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .offset(x = 17.dp, y = 157.dp)
                .height(30.dp)
                .width(130.dp),
        )
        if (seen) {
            SeenMark(x = 140.dp, y = 226.dp)
        }
    }
}

/**
 * 360x372 #DEE9FF "Алгоритмика" section. The 148x129 partner logo hangs 24dp
 * above the block's top edge on the right; only two cards are in the mock and
 * the second one bleeds past the right edge.
 */
@Composable
private fun AlgoritmikaBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(372.dp)
            .background(ToneContent),
    ) {
        AppText(
            text = stringResource(R.string.content_partners_title),
            fontSize = 20.sp,
            color = PrimaryText,
            modifier = Modifier.padding(start = 16.dp, top = 31.dp),
        )
        Illustration(
            resId = R.drawable.img_partners_logo,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-24).dp)
                .size(width = 148.dp, height = 129.dp),
            contentScale = ContentScale.Fit,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            modifier = Modifier.padding(top = 86.dp),
        ) {
            items(pinkRowCards) { pair -> SmallCard(pair.first, pair.second) }
        }
    }
}

/** 328x282 card whose media is a placeholder; the export already has the icon. */
@Composable
private fun PlaceholderCard() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(282.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Illustration(
            resId = R.drawable.img_content_card_placeholder,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth()
                .height(164.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
        )
        TagRow(y = 191.dp)
        AppText(
            text = stringResource(R.string.content_card_three_lines),
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = PrimaryText,
            maxLines = 3,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .offset(x = 17.dp, y = 211.dp)
                .padding(end = 17.dp)
                .height(60.dp)
                .fillMaxWidth(),
        )
        LikesRow(y = 267.dp)
        ProgressBar(
            progress = 0.76f,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 21.dp),
        )
    }
}
