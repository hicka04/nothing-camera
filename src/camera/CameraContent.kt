package dev.hicka04.nothingcamera.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hicka04.nothingcamera.capture.ShutterButton
import dev.hicka04.nothingcamera.preview.CameraPreviewContent
import kotlinx.coroutines.launch

/**
 * カメラのプレビュー表示と撮影を行う画面。
 */
@Composable
fun CameraContent(modifier: Modifier = Modifier) {
    val cameraState = rememberCameraState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreviewContent(
            surfaceRequest = cameraState.surfaceRequest,
            modifier = Modifier.fillMaxSize(),
        )

        ShutterButton(
            onClick = { coroutineScope.launch { cameraState.takePicture() } },
            enabled = !cameraState.isCapturing,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(bottom = 32.dp),
        )
    }
}
