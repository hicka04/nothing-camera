package dev.hicka04.nothingcamera.capture

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hicka04.nothingcamera.NothingCameraTheme
import kotlinx.coroutines.delay

/**
 * 白い内円と外リングで構成された、カメラアプリ定番の見た目のシャッターボタン。
 *
 * タップで [onTap] が呼ばれ、長押しでは押し始めに [onLongPressStart]、
 * 離した時に [onLongPressEnd] が呼ばれる。押下中は内円が縮小し、離すと元に戻る。
 */
@Composable
fun ShutterButton(
    onTap: () -> Unit,
    onLongPressStart: () -> Unit,
    onLongPressEnd: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val innerCircleScale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "ShutterButtonInnerCircleScale",
    )

    var isLongPressing by remember { mutableStateOf(false) }
    val longPressTimeoutMillis = LocalViewConfiguration.current.longPressTimeoutMillis
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(longPressTimeoutMillis)
            isLongPressing = true
            onLongPressStart()
        } else if (isLongPressing) {
            isLongPressing = false
            onLongPressEnd()
        }
    }

    Box(
        modifier = modifier
            .size(72.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                enabled = enabled,
                onClick = { if (!isLongPressing) onTap() },
            )
            .border(width = 2.dp, color = Color.White, shape = CircleShape)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(innerCircleScale)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShutterButtonPreview() {
    NothingCameraTheme {
        ShutterButton(onTap = {}, onLongPressStart = {}, onLongPressEnd = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ShutterButtonDisabledPreview() {
    NothingCameraTheme {
        ShutterButton(onTap = {}, onLongPressStart = {}, onLongPressEnd = {}, enabled = false)
    }
}
