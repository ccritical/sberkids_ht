package com.sberkids.ht.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.sberkids.ht.R
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collectLatest
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

/** All layers share one clock. Background playback is paused, and the live
 * Android animator duration scale disables playback at the resting frame.
 */
@Composable
private fun rememberTimelineTime(durationMs: Int): State<Float> {
    val clock = remember { Animatable(0f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle, durationMs) {
        val durationScale = currentCoroutineContext()[MotionDurationScale]
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { durationScale?.scaleFactor ?: 1f }.collectLatest { scale ->
                clock.snapTo(0f)
                if (scale > 0f) {
                    clock.animateTo(
                        1f,
                        infiniteRepeatable(tween(durationMs, easing = LinearEasing), RepeatMode.Restart),
                    )
                }
            }
        }
    }
    return clock.asState()
}

/** Decorative overlay: Canvas has no pointer handlers, so scrolling and taps
 * pass through. Uniform scaling preserves the design's particle trajectories.
 */
@Composable
fun PopcornMotionOverlay(modifier: Modifier = Modifier) {
    val timeline = rememberIllustrationTimeline()
    val time = rememberTimelineTime(timeline.durationMs)
    val popcorn = ImageBitmap.imageResource(R.drawable.motion_popcorn_sheet)
    val nodes = remember(timeline) { timeline.nodes.filter { it.id.startsWith("6:") } }
    Canvas(modifier.clipToBounds()) {
        val designScale = size.width / 390f
        withTransform({ scale(designScale, designScale, Offset.Zero) }) {
            nodes.forEach { drawMotionNode(it, time.value, popcorn) }
        }
    }
}

/** Fixed 140 × 176 mask with independently moving piggy and sparkles. */
@Composable
fun AnimatedPiggyIllustration(modifier: Modifier = Modifier) {
    val timeline = rememberIllustrationTimeline()
    val time = rememberTimelineTime(timeline.durationMs)
    val piggy = ImageBitmap.imageResource(R.drawable.motion_piggy)
    val sparkles = listOf(
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_1),
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_2),
        ImageBitmap.imageResource(R.drawable.motion_piggy_sparkle_3),
    )
    val nodes = remember(timeline) { timeline.nodes.filter { !it.id.startsWith("6:") } }
    Canvas(modifier.clipToBounds()) {
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
