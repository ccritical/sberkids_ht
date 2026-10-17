package com.sberkids.ht.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sberkids.ht.R

/**
 * Bottom-bar destinations, in the order they appear in the mock:
 * home, piggy banks, tasks, content.
 */
enum class MainTab(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
) {
    Home(R.string.tab_home, R.drawable.ic_24_mono_home),
    PiggyBanks(R.string.tab_piggy_banks, R.drawable.ic_24_mono_piggy),
    Tasks(R.string.tab_tasks, R.drawable.ic_24_mono_checkmark_circle),
    Content(R.string.tab_content, R.drawable.ic_24_mono_star_four),
}
