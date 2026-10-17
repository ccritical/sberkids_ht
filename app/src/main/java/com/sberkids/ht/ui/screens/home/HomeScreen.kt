package com.sberkids.ht.ui.screens.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sberkids.ht.R
import com.sberkids.ht.ui.components.HomeBackground
import com.sberkids.ht.ui.components.Illustration
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.theme.AccentBlue
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.theme.AccentGreenText
import com.sberkids.ht.ui.theme.AccentPurple
import com.sberkids.ht.ui.theme.BlobMint
import com.sberkids.ht.ui.theme.BlobPink
import com.sberkids.ht.ui.theme.BlobYellow
import com.sberkids.ht.ui.theme.BannerGradient
import com.sberkids.ht.ui.theme.bannerBrush
import com.sberkids.ht.ui.theme.BonusPill
import com.sberkids.ht.ui.theme.HistoryBadge
import com.sberkids.ht.ui.theme.HistoryIcon
import com.sberkids.ht.ui.theme.PrimaryText
import com.sberkids.ht.ui.theme.SecondaryText
import com.sberkids.ht.ui.theme.Surface
import com.sberkids.ht.ui.theme.TertiaryText

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    HomeBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // The bar scrolls with the content: it is the first list item, not
            // a pinned overlay.
            item { MainTopBar(height = 88.dp) }
            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 13.dp),
                ) {
                    BalanceCard()
                    Spacer(Modifier.height(40.dp))
                    QuickActionsRow()
                    Spacer(Modifier.height(40.dp))
                }
            }
            item { PromoBanner() }
            item { Spacer(Modifier.height(15.dp)) }
            item { HistoryBlock() }
        }
    }
}

/**
 * Card zone, 358x234.8. The mock paints no white surface behind it — the page
 * blobs show through. Only the 72dp balance strip is white; the artwork above
 * it is the card illustration itself.
 */
@Composable
private fun BalanceCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(234.8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.8.dp),
        ) {
            Illustration(
                resId = R.drawable.img_monsters,
                modifier = Modifier
                    .offset(x = 61.8.dp, y = 0.dp)
                    .width(237.5.dp)
                    .height(159.6.dp),
            )
            Illustration(
                resId = R.drawable.img_sbercat,
                modifier = Modifier
                    .offset(x = 195.9.dp, y = 34.2.dp)
                    .width(155.2.dp)
                    .height(115.2.dp)
                    .alpha(0.32f),
            )
        }

        // White balance strip: 358x72, 18dp corners on all four sides.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 25.1.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = stringResource(R.string.home_balance_amount),
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                    )
                    Text(
                        text = stringResource(R.string.home_balance_cents),
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryText,
                    )
                }
                // Bonus pill: 102x56, pale lime.
                Row(
                    modifier = Modifier
                        .width(102.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BonusPill)
                        .padding(start = 24.9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.home_bonus_amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                    )
                    Spacer(Modifier.width(2.dp))
                    TintedIcon(
                        resId = R.drawable.ic_24_mono_spasibo,
                        tint = PrimaryText,
                        size = 15.dp,
                    )
                }
            }
        }

        // Pager dots sit under the strip at y=228.8: 28x6 plus two 6x6, all black.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 28.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(PrimaryText),
            )
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(PrimaryText),
            )
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(PrimaryText),
            )
        }
    }
}

@Composable
private fun QuickActionsRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 72x122 tile with the QR badge and a short label.
        item {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface),
            ) {
                Text(
                    text = stringResource(R.string.home_action_qr_short),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    modifier = Modifier.padding(start = 26.dp, top = 20.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 62.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(com.sberkids.ht.ui.theme.BadgeLilac),
                    contentAlignment = Alignment.Center,
                ) {
                    TintedIcon(resId = R.drawable.ic_24_mono_qr, tint = AccentPurple, size = 20.dp)
                }
            }
        }
        item {
            ActionTile(
                label = stringResource(R.string.home_action_send),
                iconRes = R.drawable.ic_24_mono_arrow_right,
                badge = com.sberkids.ht.ui.theme.BadgeSky,
                tint = AccentBlue,
            )
        }
        item {
            ActionTile(
                label = stringResource(R.string.home_action_mobile),
                iconRes = R.drawable.ic_24_mono_mobile,
                badge = com.sberkids.ht.ui.theme.BadgeMint,
                tint = BlobMint,
            )
        }
        // Contacts: two coloured blobs with faces plus an avatar.
        item { ContactTile(fill = BlobPink) }
        item { ContactTile(fill = BlobMint) }
        item {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface),
                contentAlignment = Alignment.TopCenter,
            ) {
                Illustration(
                    resId = R.drawable.img_star_avatar,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(48.dp),
                )
            }
        }
    }
}

@Composable
private fun ActionTile(
    label: String,
    iconRes: Int,
    badge: Color,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .width(144.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText,
            modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 16.dp),
        )
        Box(
            modifier = Modifier
                .padding(start = 84.dp, top = 60.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(badge),
            contentAlignment = Alignment.Center,
        ) {
            TintedIcon(resId = iconRes, tint = tint, size = 20.dp)
        }
    }
}

/** Small smiley contact: coloured round blob with two eyes. */
@Composable
private fun ContactTile(fill: Color) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface),
        contentAlignment = Alignment.TopCenter,
    ) {
        Canvas(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(48.dp),
        ) {
            drawCircle(fill)
            val eye = size.width * 0.10f
            drawOval(
                color = Color.White,
                topLeft = Offset(size.width * 0.22f, size.height * 0.36f),
                size = Size(size.width * 0.22f, size.height * 0.15f),
            )
            drawOval(
                color = Color.White,
                topLeft = Offset(size.width * 0.56f, size.height * 0.36f),
                size = Size(size.width * 0.22f, size.height * 0.15f),
            )
            drawCircle(Color.White, radius = eye, center = Offset(size.width * 0.33f, size.height * 0.43f))
            drawCircle(Color.White, radius = eye, center = Offset(size.width * 0.67f, size.height * 0.43f))
        }
    }
}

/**
 * 358x104 promo banner. The title is two separate text vectors in the mock
 * (35.4 tall headline and 11.2 tall caption), 8dp apart, both white.
 */
@Composable
private fun PromoBanner() {
    // The gradient handles are normalised to the node, so the brush needs the
    // real pixel size of this composable; dp would be density-independent.
    var bannerSize by remember { mutableStateOf(IntSize.Zero) }
    val bannerBrushState = remember(bannerSize) {
        if (bannerSize == IntSize.Zero) null
        else bannerBrush(bannerSize.width.toFloat(), bannerSize.height.toFloat())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .onSizeChanged { bannerSize = it }
            .clip(RoundedCornerShape(24.dp))
            .background(bannerBrushState ?: Brush.linearGradient(BannerGradient)),
    ) {
        Illustration(
            resId = R.drawable.img_banner_mascot,
            modifier = Modifier
                .offset(x = 244.dp, y = (-18).dp)
                .width(114.dp)
                .height(140.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.2.dp, top = 25.4.dp)
                .width(174.3.dp),
        ) {
            Text(
                text = stringResource(R.string.home_banner_title),
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_banner_text),
                fontSize = 11.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
            )
        }
        TintedIcon(
            resId = R.drawable.ic_16_mono_cross,
            tint = Color.White,
            size = 10.dp,
            modifier = Modifier
                .offset(x = 333.1.dp, y = 15.1.dp),
        )
    }
}

private data class HistoryRow(
    val title: String,
    val description: String,
    val amount: String,
    val iconRes: Int,
    val positive: Boolean,
    val bonus: String? = null,
)

/**
 * History block from Figma node `1:10401` (414x418 at y=657):
 *   caption 0..56, label baseline 14, x=17.8
 *   card    66..322 (358x256, r=16, white)
 *   rows    86..286, 100dp pitch, icon 48dp at x=36, text at x=104
 *   button  322..378 (358x56, r=20, white)
 * The container fades into the page: the mock overlays a vertical gradient
 * from transparent to #F2F5F7, hence the gap above the card and the fade below.
 */
@Composable
private fun HistoryBlock() {
    val rows = listOf(
        HistoryRow(
            title = stringResource(R.string.home_history_1_title),
            description = stringResource(R.string.home_history_1_subtitle),
            amount = stringResource(R.string.home_history_1_amount),
            iconRes = R.drawable.ic_24_mono_bed,
            positive = false,
            bonus = stringResource(R.string.home_history_1_bonus),
        ),
        HistoryRow(
            title = stringResource(R.string.home_history_2_title),
            description = stringResource(R.string.home_history_2_subtitle),
            amount = stringResource(R.string.home_history_2_amount),
            iconRes = R.drawable.ic_24_mono_clock_circle,
            positive = false,
        ),
        HistoryRow(
            title = stringResource(R.string.home_history_3_title),
            description = stringResource(R.string.home_history_3_subtitle),
            amount = stringResource(R.string.home_history_3_amount),
            iconRes = R.drawable.ic_24_mono_outgoing,
            positive = false,
        ),
    )
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(R.string.home_history_title),
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                modifier = Modifier.padding(start = 17.8.dp),
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(256.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface),
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                rows.forEach { row -> HistoryCell(row) }
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.home_history_show_all),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryText,
            )
        }
    }
}

@Composable
private fun HistoryCell(row: HistoryRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(HistoryBadge),
            contentAlignment = Alignment.Center,
        ) {
            TintedIcon(resId = row.iconRes, tint = HistoryIcon, size = 21.dp)
        }
        Column(
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(1f),
        ) {
            Text(
                text = row.title,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                maxLines = 1,
            )
            Text(
                text = row.description,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                maxLines = 1,
            )
        }
        // Bonus badge sits under the amount, right-aligned, only when present.
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = row.amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (row.positive) AccentGreenText else PrimaryText,
            )
            if (row.bonus != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = row.bonus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentGreenText,
                    )
                    Spacer(Modifier.width(3.dp))
                    TintedIcon(
                        resId = R.drawable.ic_24_mono_spasibo,
                        tint = AccentGreen,
                        size = 14.dp,
                    )
                }
            }
        }
    }
}
