package dev.hicka04.nothingcamera.preview

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * カメラのプレビュー映像を全画面に表示する。
 *
 * [surfaceRequest] が `null` の間（バインド前など）は何も表示しない。
 */
@Composable
fun CameraPreviewContent(surfaceRequest: SurfaceRequest?, modifier: Modifier = Modifier) {
    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = modifier.fillMaxSize(),
        )
    }
}
