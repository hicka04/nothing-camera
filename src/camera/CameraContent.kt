package dev.hicka04.nothingcamera.camera

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.hicka04.nothingcamera.preview.CameraPreviewContent

/**
 * カメラのプレビュー表示と撮影を行う画面。
 */
@Composable
fun CameraContent(modifier: Modifier = Modifier) {
    val cameraState = rememberCameraState()

    CameraPreviewContent(
        surfaceRequest = cameraState.surfaceRequest,
        modifier = modifier.fillMaxSize(),
    )
}
