package com.sberkids.ht.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sberkids.ht.R
import androidx.compose.ui.res.stringResource
import com.sberkids.ht.ui.components.MainTopBar
import com.sberkids.ht.ui.components.TintedIcon
import com.sberkids.ht.ui.components.TopBarAction
import com.sberkids.ht.ui.theme.AccentGreen
import com.sberkids.ht.ui.components.MainBottomBar
import com.sberkids.ht.ui.navigation.MainTab
import com.sberkids.ht.ui.screens.content.ContentScreen
import com.sberkids.ht.ui.screens.home.HomeScreen
import com.sberkids.ht.ui.screens.piggybanks.PiggyBanksScreen
import com.sberkids.ht.ui.screens.tasks.TasksScreen
import com.sberkids.ht.ui.theme.AppBackground
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.core.content.ContextCompat
import com.sberkids.ht.ui.theme.MOCK_WEIGHT
import com.sberkids.ht.ui.theme.MOCK_SPACING_EM
import com.sberkids.ht.ui.theme.Inter14
import com.sberkids.ht.ui.theme.SberKidsHtTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    val popcornTrigger = rememberPowerConnectionTrigger()
    var popcornLastStartedTrigger by remember { mutableIntStateOf(0) }


    // Base style for anything that does not set a size. Sized text goes through
    // AppText, which picks the optical size that matches its size.
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = Inter14,
            fontWeight = FontWeight(MOCK_WEIGHT),
            letterSpacing = MOCK_SPACING_EM.em,
        ),
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
                // Slide horizontally in the direction of travel and fade at the
                // same time. A plain Crossfade only dims one screen into the
                // other, which reads as a hard cut now that each tab owns its
                // own scrolling top bar.
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        val dir = if (forward) 1 else -1
                        (
                            slideInHorizontally(
                                animationSpec = tween(320, easing = FastOutSlowInEasing),
                            ) { width -> dir * width / 4 } +
                                fadeIn(tween(320))
                            ) togetherWith (
                            slideOutHorizontally(
                                animationSpec = tween(320, easing = FastOutSlowInEasing),
                            ) { width -> -dir * width / 4 } +
                                fadeOut(tween(220))
                            )
                    },
                    label = "tabContent",
                ) { tab ->
                    when (tab) {
                        MainTab.Home -> HomeScreen(
                            popcornTrigger = popcornTrigger,
                            popcornLastStartedTrigger = popcornLastStartedTrigger,
                            onPopcornAnimationStarted = {
                                popcornLastStartedTrigger = popcornTrigger
                            },
                        )
                        MainTab.PiggyBanks -> PiggyBanksScreen()
                        MainTab.Tasks -> TasksScreen()
                        MainTab.Content -> ContentScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberPowerConnectionTrigger(): Int {
    val context = LocalContext.current.applicationContext
    val trigger = remember { mutableIntStateOf(0) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_POWER_CONNECTED) trigger.intValue++
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(Intent.ACTION_POWER_CONNECTED),
            ContextCompat.RECEIVER_EXPORTED,
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

    return trigger.intValue
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1182)
@Composable
private fun MainScreenPreview() {
    SberKidsHtTheme {
        MainScreen()
    }
}
