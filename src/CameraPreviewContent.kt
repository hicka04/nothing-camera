package dev.hicka04.nothingcamera

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** 背面カメラのプレビュー映像を全画面に表示する。 */
@Composable
fun CameraPreviewContent(modifier: Modifier = Modifier) {
    val viewModel: CameraPreviewViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = modifier.fillMaxSize(),
        )
    }
}
