package dev.hicka04.nothingcamera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NothingCameraScreen()
        }
    }
}
