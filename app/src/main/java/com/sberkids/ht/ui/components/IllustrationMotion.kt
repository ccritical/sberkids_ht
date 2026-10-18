package com.sberkids.ht.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Constraints
import android.os.SystemClock
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sberkids.ht.R
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

/** Playback of Figma timeline 2:6675. Coordinates and translations share the
 * design space. Rotation tracks are absolute, including the static rotation.
 */
private data class MotionFrame(val time: Float, val values: FloatArray, val easing: Easing)

private class MotionTrack(private val frames: List<MotionFrame>) {
    fun value(time: Float, channel: Int, fallback: Float): Float {
        if (frames.isEmpty()) return fallback
        if (time <= frames.first().time) return frames.first().values[channel]
        var low = 0
        var high = frames.lastIndex
        while (low < high) {
            val middle = (low + high + 1) / 2
            if (frames[middle].time <= time) low = middle else high = middle - 1
        }
        val from = frames[low]
        if (low == frames.lastIndex) return from.values[channel]
        val to = frames[low + 1]
        val fraction = from.easing.transform((time - from.time) / (to.time - from.time))
        return from.values[channel] + (to.values[channel] - from.values[channel]) * fraction
    }
}

private data class ImageBounds(val left: Float, val top: Float, val width: Float, val height: Float)

private data class MotionNode(
    val id: String,
    val width: Float,
    val height: Float,
    val center: Offset,
    val baseRotation: Float,
    val tracks: Map<String, MotionTrack>,
    val imageBounds: ImageBounds,
    val clip: Path?,
) {
    fun value(track: String, time: Float, channel: Int = 0, fallback: Float = 0f) =
        tracks[track]?.value(time, channel, fallback) ?: fallback
}

private data class IllustrationTimeline(val durationMs: Int, val nodes: List<MotionNode>)

@Composable
private fun rememberIllustrationTimeline(): IllustrationTimeline {
    val resources = LocalResources.current
    return remember(resources) {
        val json = resources.openRawResource(R.raw.illustration_motion).bufferedReader().use {
            JSONObject(it.readText())
        }
        val nodes = json.getJSONArray("nodes")
        IllustrationTimeline(json.getInt("durationMs"), List(nodes.length()) { index ->
            val node = nodes.getJSONObject(index)
            val width = node.float("width")
            val height = node.float("height")
            val crop = node.optJSONObject("crop")
            val bounds = node.optJSONObject("imageBounds")
            val tracks = node.getJSONObject("tracks")
            MotionNode(
                id = node.getString("id"), width = width, height = height,
                center = Offset(node.float("centerX"), node.float("centerY")),
                baseRotation = node.float("baseRotation"),
                tracks = tracks.keys().asSequence().associateWith { key ->
                    val frames = tracks.getJSONArray(key)
                    MotionTrack(List(frames.length()) { frameIndex ->
                        val frame = frames.getJSONObject(frameIndex)
                        MotionFrame(frame.float("t"), frame.getJSONArray("v").floats(), easing(frame.getString("e")))
                    })
                },
                imageBounds = when {
                    crop != null -> ImageBounds(
                        crop.float("left") * width, crop.float("top") * height,
                        crop.float("width") * width, crop.float("height") * height,
                    )
                    bounds != null -> ImageBounds(
                        bounds.float("left"), bounds.float("top"),
                        bounds.float("width"), bounds.float("height"),
                    )
                    else -> ImageBounds(0f, 0f, width, height)
                },
                clip = crop?.let {
                    Path().apply {
                        addRoundRect(RoundRect(0f, 0f, width, height, CornerRadius(it.float("radius"))))
                    }
                },
            )
        })
    }
}

private fun JSONObject.float(key: String) = getDouble(key).toFloat()
private fun JSONArray.floats() = FloatArray(length()) { getDouble(it).toFloat() }

private fun easing(css: String): Easing = when (css) {
    "linear" -> LinearEasing
    "ease-in-out" -> CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    "ease-out" -> CubicBezierEasing(0f, 0f, 0.58f, 1f)
    else -> {
        require(css.startsWith("cubic-bezier(")) { "Unsupported Figma easing: $css" }
        val points = css.substringAfter('(').substringBefore(')').split(',').map { it.trim().toFloat() }
        CubicBezierEasing(points[0], points[1], points[2], points[3])
    }
}

/** Plays one timeline pass whenever [trigger] changes, then returns to frame 0. */
@Composable
private fun rememberTriggeredTimelineTime(
    durationMs: Int,
    trigger: Int,
    shouldPlay: Boolean,
    onAnimationStarted: () -> Unit = {},
): State<Float> {
    val clock = remember { Animatable(0f) }
    val onAnimationStartedState = rememberUpdatedState(onAnimationStarted)
    LaunchedEffect(durationMs, trigger) {
        if (trigger > 0 && shouldPlay) {
            clock.snapTo(0f)
            onAnimationStartedState.value()
            clock.animateTo(1f, tween(durationMs, easing = LinearEasing))
            clock.snapTo(0f)
        }
    }
    return clock.asState()
}

/** Decorative overlay: Canvas has no pointer handlers, so scrolling and taps
 * pass through. Uniform scaling preserves the design's particle trajectories.
 */
@Composable
fun PopcornMotionOverlay(
    modifier: Modifier = Modifier,
    trigger: Int = 0,
    lastStartedTrigger: Int = 0,
    onAnimationStarted: () -> Unit = {},
) {
    val timeline = rememberIllustrationTimeline()
    val time = rememberTriggeredTimelineTime(
        durationMs = timeline.durationMs,
        trigger = trigger,
        shouldPlay = trigger > lastStartedTrigger,
        onAnimationStarted = onAnimationStarted,
    )
    val popcorn = ImageBitmap.imageResource(R.drawable.motion_popcorn_sheet)
    val nodes = remember(timeline) { timeline.nodes.filter { it.id.startsWith("6:") } }
    Canvas(modifier.clipToBounds()) {
        val designScale = size.width / 390f
        withTransform({ scale(designScale, designScale, Offset.Zero) }) {
            nodes.forEach { drawMotionNode(it, time.value, popcorn) }
        }
    }
}

/** 140 × 176 illustration with extra room around the break animation. */
@Composable
fun AnimatedPiggyIllustration(modifier: Modifier = Modifier) {
    val timeline = rememberIllustrationTimeline()
    var trigger by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    var completedShakes by remember { mutableIntStateOf(0) }
    var showBreakAnimation by remember { mutableStateOf(false) }
    val time = remember { Animatable(0f) }
    // Preload Lottie while the user taps and the shake animations play.
    val breakComposition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.piggy_break))
    val breakAnimation = rememberLottieAnimatable()
    val breakBlend = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect

        time.snapTo(0f)
        time.animateTo(1f, tween(timeline.durationMs, easing = LinearEasing))
        time.snapTo(0f)
        completedShakes++

        // Keep the input locked through the entire third shake and break.
        if (completedShakes == PIGGY_SHAKES_BEFORE_BREAK) {
            val composition = breakComposition.await()
            breakAnimation.snapTo(composition, progress = 0f)
            showBreakAnimation = true
            breakBlend.animateTo(1f, tween(PIGGY_BREAK_TRANSITION_MS))
            breakAnimation.animate(composition, iterations = 1)
            breakBlend.animateTo(0f, tween(PIGGY_BREAK_TRANSITION_MS))
            showBreakAnimation = false
            completedShakes = 0
        }
        isAnimating = false
    }
    val piggy = ImageBitmap.imageResource(R.drawable.motion_piggy)
    val sparkles = listOf(
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_1),
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_2),
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_3),
    )
    val nodes = remember(timeline) { timeline.nodes.filter { !it.id.startsWith("6:") } }
    Box(
        modifier
            .pointerInput(Unit) {
                var tapCount = 0
                var lastTapAt = 0L
                detectTapGestures {
                    if (isAnimating) {
                        // Taps during playback neither restart nor queue a shake.
                        tapCount = 0
                        lastTapAt = 0L
                        return@detectTapGestures
                    }
                    val now = SystemClock.uptimeMillis()
                    tapCount = if (now - lastTapAt > TRIPLE_TAP_TIMEOUT_MS) 1 else tapCount + 1
                    lastTapAt = now
                    if (tapCount == PIGGY_SHAKE_TAP_COUNT) {
                        isAnimating = true
                        trigger++
                        tapCount = 0
                        lastTapAt = 0L
                    }
                }
            },
    ) {
        Canvas(
            Modifier.fillMaxSize().clipToBounds().graphicsLayer { alpha = 1f - breakBlend.value },
        ) {
            withTransform({
                scale(size.width / 140f, size.height / 176f, Offset.Zero)
                translate(-220f, -112f)
            }) {
                // Figma stacking: the first sparkle is behind the pig.
                nodes.forEach { node ->
                    val bitmap = when (node.id) {
                        "33:24" -> piggy
                        "2:9400" -> sparkles[0]
                        "2:9405" -> sparkles[1]
                        else -> sparkles[2]
                    }
                    drawMotionNode(node, time.value, bitmap)
                }
            }
        }
        if (showBreakAnimation) {
            LottieAnimation(
                composition = breakComposition.value,
                progress = { breakAnimation.progress },
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val scaleX = constraints.maxWidth / 140f
                        val scaleY = constraints.maxHeight / 176f
                        // The shared 344px pig is drawn at 172dp. In Lottie its
                        // top-left is (268,341); the existing illustration uses
                        // (-19,19). Map those exact bounds instead of fitting
                        // the entire 880px composition into the illustration.
                        // Let Lottie use its own composition bounds so the
                        // scattered pieces can leave the resting pig's mask.
                        val animation = measurable.measure(
                            Constraints.fixed(
                                (440f * scaleX).roundToInt(),
                                (440f * scaleY).roundToInt(),
                            ),
                        )
                        layout(constraints.maxWidth, constraints.maxHeight) {
                            animation.place(
                                (-153f * scaleX).roundToInt(),
                                (-151.5f * scaleY).roundToInt(),
                            )
                        }
                    }
                    .graphicsLayer { alpha = breakBlend.value },
            )
        }
    }
}

private fun DrawScope.drawMotionNode(node: MotionNode, time: Float, bitmap: ImageBitmap) {
    val opacity = node.value("opacity", time, fallback = 1f).coerceIn(0f, 1f)
    if (opacity == 0f) return
    withTransform({
        translate(
            node.center.x + node.value("translate", time),
            node.center.y + node.value("translate", time, channel = 1),
        )
        rotate(node.value("rotate", time, fallback = node.baseRotation / RADIANS_TO_DEGREES) * RADIANS_TO_DEGREES, Offset.Zero)
        scale(
            node.value("scale", time, fallback = 1f),
            node.value("scale", time, channel = 1, fallback = 1f),
            Offset.Zero,
        )
        translate(-node.width / 2f, -node.height / 2f)
    }) {
        fun drawBitmap() {
            val bounds = node.imageBounds
            drawImage(
                image = bitmap,
                dstOffset = IntOffset(bounds.left.roundToInt(), bounds.top.roundToInt()),
                dstSize = IntSize(bounds.width.roundToInt(), bounds.height.roundToInt()),
                alpha = opacity,
                filterQuality = FilterQuality.High,
            )
        }
        node.clip?.let { clipPath(it) { drawBitmap() } } ?: drawBitmap()
    }
}

private const val RADIANS_TO_DEGREES = 57.29578f
private const val TRIPLE_TAP_TIMEOUT_MS = 800L
private const val PIGGY_SHAKE_TAP_COUNT = 3
private const val PIGGY_SHAKES_BEFORE_BREAK = 3
private const val PIGGY_BREAK_TRANSITION_MS = 160
