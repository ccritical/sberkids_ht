package com.sberkids.ht.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.core.content.ContextCompat
import com.sberkids.ht.ui.components.MainBottomBar
import com.sberkids.ht.ui.components.ChargingBoltsOverlay
import com.sberkids.ht.ui.components.PopcornMotionOverlay
import com.sberkids.ht.ui.navigation.MainTab
import com.sberkids.ht.ui.screens.content.ContentScreen
import com.sberkids.ht.ui.screens.home.HomeScreen
import com.sberkids.ht.ui.screens.piggybanks.PiggyBanksScreen
import com.sberkids.ht.ui.screens.tasks.TasksScreen
import com.sberkids.ht.ui.theme.AppBackground
import com.sberkids.ht.ui.theme.Inter14
import com.sberkids.ht.ui.theme.MOCK_SPACING_EM
import com.sberkids.ht.ui.theme.MOCK_WEIGHT
import com.sberkids.ht.ui.theme.SberKidsHtTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var completedGoalWithdrawn by rememberSaveable { mutableStateOf(false) }
    var popcornTrigger by remember { mutableIntStateOf(0) }
    var popcornLastStartedTrigger by remember { mutableIntStateOf(0) }
    val chargingTrigger = rememberPowerConnectionTrigger()
    var chargingLastStartedTrigger by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Base style for anything that does not set a size. Sized text goes through
    // AppText, which picks the optical size that matches its size.
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = Inter14,
            fontWeight = FontWeight(MOCK_WEIGHT),
            letterSpacing = MOCK_SPACING_EM.em,
        ),
    ) {
        Box(modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
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
                            MainTab.Home -> HomeScreen()
                            MainTab.PiggyBanks -> PiggyBanksScreen(
                                showCompletedGoal = !completedGoalWithdrawn,
                                onWithdrawClick = {
                                    if (!completedGoalWithdrawn) {
                                        completedGoalWithdrawn = true
                                        popcornTrigger++
                                    }
                                },
                            )
                            MainTab.Tasks -> TasksScreen()
                            MainTab.Content -> ContentScreen()
                        }
                    }
                }
            }

            // Only the completed goal's withdrawal button triggers popcorn.
            // Leaving savings cancels playback without replaying it on return.
            if (selectedTab == MainTab.PiggyBanks) {
                PopcornMotionOverlay(
                    modifier = Modifier.matchParentSize(),
                    trigger = popcornTrigger,
                    lastStartedTrigger = popcornLastStartedTrigger,
                    onAnimationStarted = { popcornLastStartedTrigger = popcornTrigger },
                )
            }

            // Draw above the entire scaffold, including its bottom navigation.
            ChargingBoltsOverlay(
                modifier = Modifier.matchParentSize(),
                trigger = chargingTrigger,
                lastStartedTrigger = chargingLastStartedTrigger,
                onAnimationStarted = {
                    chargingLastStartedTrigger = chargingTrigger
                    vibrateForCharging(context)
                },
            )
        }
    }
}

private fun vibrateForCharging(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    } ?: return

    if (vibrator.hasVibrator()) {
        vibrator.vibrate(VibrationEffect.createOneShot(18L, 40))
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
