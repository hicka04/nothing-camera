package dev.hicka04.nothingcamera.preview

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation

/**
 * 背面カメラのプレビュー映像を全画面に表示する。
 *
 * [Preview] ユースケースの生成からライフサイクルへのバインドまでを composable 内で完結させる。
 * バインドは [LocalLifecycleOwner] に従うため、バックグラウンド遷移時は自動的に unbind され、
 * フォアグラウンド復帰時に自動的に再バインドされる。
 */
@Composable
fun CameraPreviewContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    val previewUseCase = remember {
        Preview.Builder().build().apply {
            setSurfaceProvider { newSurfaceRequest -> surfaceRequest = newSurfaceRequest }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)
        processCameraProvider.bindToLifecycle(lifecycleOwner, DEFAULT_BACK_CAMERA, previewUseCase)

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = modifier.fillMaxSize(),
        )
    }
}
