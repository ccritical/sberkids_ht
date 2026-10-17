package com.sberkids.ht.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.sberkids.ht.ui.components.MainBottomBar
import com.sberkids.ht.ui.navigation.MainTab
import com.sberkids.ht.ui.screens.content.ContentScreen
import com.sberkids.ht.ui.screens.home.HomeScreen
import com.sberkids.ht.ui.screens.piggybanks.PiggyBanksScreen
import com.sberkids.ht.ui.screens.tasks.TasksScreen
import com.sberkids.ht.ui.theme.AppBackground
import com.sberkids.ht.ui.theme.Inter
import com.sberkids.ht.ui.theme.SberKidsHtTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    // Every Text in the app inherits Inter from here, including call sites that
    // only set fontSize/weight.
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = Inter),
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = AppBackground,
            bottomBar = {
                MainBottomBar(
                    selected = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onAssistantClick = {},
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
            ) {
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(180),
                    label = "tabContent",
                ) { tab ->
                    when (tab) {
                        MainTab.Home -> HomeScreen()
                        MainTab.PiggyBanks -> PiggyBanksScreen()
                        MainTab.Tasks -> TasksScreen()
                        MainTab.Content -> ContentScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1182)
@Composable
private fun MainScreenPreview() {
    SberKidsHtTheme {
        MainScreen()
    }
}
