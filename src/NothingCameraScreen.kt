package dev.hicka04.nothingcamera

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dev.hicka04.nothingcamera.permission.CameraPermissionDeniedContent
import dev.hicka04.nothingcamera.permission.CameraPermissionStatus
import dev.hicka04.nothingcamera.permission.rememberCameraPermissionState
import dev.hicka04.nothingcamera.preview.CameraPreviewContent

@Composable
fun NothingCameraScreen() {
    MaterialTheme {
        val permissionState = rememberCameraPermissionState()
        LaunchedEffect(Unit) {
            permissionState.request()
        }

        if (permissionState.status == CameraPermissionStatus.Granted) {
            CameraPreviewContent(modifier = Modifier.fillMaxSize())
        } else {
            CameraPermissionDeniedContent(
                permissionState = permissionState,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
