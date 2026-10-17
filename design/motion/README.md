# Illustration motion

Source: [Figma canvas timeline, 2:6675](https://www.figma.com/design/5tdk5pxaQS0u0xMcEznPd7/Untitled?node-id=2-6675).

`app/src/main/res/raw/illustration_motion.json` is exported animation data for
the existing Home and Piggy Banks screens: 18 popcorn particles, the piggy and
three sparkles. It preserves the CSS snippet keyframes, segment easing, original
node IDs, sprite crops and layer order from `get_motion_context` and
`get_design_context`. The shared canvas timeline loops every 2400 ms. The
standalone Pop Corn demo (2:9457) and standalone piggy demo (33:26) have different
timelines and are not the source for these screen illustrations.

Duplicate CSS percentages use the final declaration, as in CSS. Angles are
absolute radians; the renderer converts to degrees without adding static rotation
again. Particle positions use their original transformed centers and share one
uniform 390-wide design space. The piggy retains the fixed 140 × 176 clip at
(220, 112), with independently animated inner layers.

`motion_popcorn_sheet.png` is the original uploaded sprite sheet, used with the
exported crop rectangles. `motion_piggy.png` is the original separate piggy image.
Figma's PNG sparkle exports had an opaque dark background. The SVG assets kept
here were rasterized with Sharp at 288 dpi into transparent Android PNG resources;
their original stroke/render bounds are retained in the motion data.

Playback uses the native Compose frame clock, pauses while the activity is not
resumed and shows the resting frame when Android's animator duration scale is 0.
The popcorn overlay is decorative and does not intercept touch or scroll input.

Set `JAVA_HOME` to a JDK 21 installation, then build and check:

```sh
./gradlew :app:assembleDebug :app:lintDebug
```
