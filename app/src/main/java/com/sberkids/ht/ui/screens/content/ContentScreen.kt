package com.sberkids.ht.ui.screens.content

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
import com.sberkids.ht.ui.components.AppCard
import com.sberkids.ht.ui.components.Footnote
import com.sberkids.ht.ui.components.Illustration
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.LabelChip
import com.sberkids.ht.ui.components.ProgressBar
import com.sberkids.ht.ui.components.ScreenBackground
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.AccentPurple
import com.sberkids.ht.ui.theme.BlobPurple
import com.sberkids.ht.ui.theme.BlobPurpleLight
import com.sberkids.ht.ui.theme.CtaGradient
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.SecondaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TertiaryText
import com.sberkids.ht.ui.theme.ToneContent

@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    ScreenBackground(
        primary = BlobPurple,
        secondary = BlobPurpleLight,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { MainTopBar() }
            item { Spacer(Modifier.height(8.dp)) }
            item { StoryCarousel() }
            item { CategoryChips() }
            item { StreamPlayer() }
            item { FeaturedCard() }
            item { InfoBanner() }
            item { TopFiveHeader() }
            item { TopFiveRow() }
            item { WideCard() }
            item { PartnersBanner() }
            item { CompactCard() }
        }
    }
}

private val storyIllustrations = listOf(
    R.drawable.img_story_1,
    R.drawable.img_story_2,
    R.drawable.img_story_3,
    R.drawable.img_story_4,
    R.drawable.img_stream_cover,
)

@Composable
private fun StoryCarousel() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(storyIllustrations) { illustration ->
            Illustration(
                resId = illustration,
                modifier = Modifier
                    .size(width = 80.dp, height = 100.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )
        }
    }
}

private data class Chip(val label: String, val selected: Boolean)

@Composable
private fun CategoryChips() {
    val chips = listOf(
        Chip(stringResource(R.string.content_chip_all), true),
        Chip(stringResource(R.string.content_chip_popular), false),
        Chip(stringResource(R.string.content_chip_new), false),
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(chips) { chip ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (chip.selected) PrimaryText else Surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = chip.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (chip.selected) Color.White else PrimaryText,
                )
            }
        }
    }
}

/** 328x140 player card with a cover and a title. */
@Composable
private fun StreamPlayer() {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        AppCard(modifier = Modifier.fillMaxWidth().height(140.dp), corner = 24.dp) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Illustration(
                    resId = R.drawable.img_stream_cover,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.content_stream_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText,
                    )
                    Spacer(Modifier.height(4.dp))
                    Footnote(stringResource(R.string.content_stream_subtitle), color = SecondaryText)
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(CtaGradient))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.content_chip_all),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedCard() {
    ContentCardLayout(
        image = R.drawable.img_content_card_1,
        tag = stringResource(R.string.content_tag_article),
        title = stringResource(R.string.content_card_long),
        progress = 0.76f,
        likes = stringResource(R.string.content_likes),
        height = 296.dp,
    )
}

@Composable
private fun WideCard() {
    ContentCardLayout(
        image = R.drawable.img_content_card_2,
        tag = stringResource(R.string.content_tag_video),
        title = stringResource(R.string.content_card_short),
        progress = 0.32f,
        likes = stringResource(R.string.content_likes),
        height = 278.dp,
    )
}

@Composable
private fun CompactCard() {
    ContentCardLayout(
        image = R.drawable.img_content_card_3,
        tag = stringResource(R.string.content_tag_video),
        title = stringResource(R.string.content_card_long),
        progress = 0.12f,
        likes = stringResource(R.string.content_likes),
        height = 282.dp,
    )
}

@Composable
private fun ContentCardLayout(
    image: Int,
    tag: String,
    title: String,
    progress: Float,
    likes: String,
    height: androidx.compose.ui.unit.Dp,
) {
    AppCard(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(height),
        corner = 24.dp,
    ) {
        Column(Modifier.fillMaxSize()) {
            Illustration(
                resId = image,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth()
                    .height(164.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 17.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressBar(
                        progress = progress,
                        modifier = Modifier.width(74.dp),
                        height = 6.dp,
                        fill = AccentPurple,
                    )
                    Spacer(Modifier.width(12.dp))
                    LabelChip(text = tag)
                    Spacer(Modifier.width(8.dp))
                    TintedIcon(
                        resId = R.drawable.ic_24_mono_heart,
                        tint = TertiaryText,
                        size = 12.dp,
                    )
                    Spacer(Modifier.width(4.dp))
                    Footnote(likes)
                }
            }
        }
    }
}

/** 328x104 gradient promo banner. */
@Composable
private fun InfoBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(com.sberkids.ht.ui.theme.BannerGradient)),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, end = 120.dp),
        ) {
            Text(
                text = stringResource(R.string.content_banner_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.content_banner_text),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color.White.copy(alpha = 0.92f),
            )
        }
        Illustration(
            resId = R.drawable.img_banner_mascot,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 114.dp, height = 104.dp),
        )
    }
}

@Composable
private fun TopFiveHeader() {
    Text(
        text = stringResource(R.string.content_top5_title),
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryText,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun TopFiveRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(listOf(R.drawable.img_top5_1, R.drawable.img_top5_2, R.drawable.img_content_wide_1)) { image ->
            AppCard(modifier = Modifier.size(width = 180.dp, height = 262.dp), corner = 24.dp) {
                Column(Modifier.fillMaxSize()) {
                    Illustration(
                        resId = image,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(164.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    )
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.content_card_long),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText,
                            maxLines = 2,
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TintedIcon(
                                resId = R.drawable.ic_24_mono_heart,
                                tint = TertiaryText,
                                size = 12.dp,
                            )
                            Spacer(Modifier.width(4.dp))
                            Footnote(stringResource(R.string.content_likes))
                        }
                    }
                }
            }
        }
    }
}

/** 360x372 partners block. */
@Composable
private fun PartnersBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(372.dp)
            .background(ToneContent),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(R.string.content_partners_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            )
            Spacer(Modifier.height(4.dp))
            Footnote(stringResource(R.string.content_ad), color = SecondaryText)
        }
        Illustration(
            resId = R.drawable.img_partners,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(20.dp)),
        )
    }
}
