package dev.hicka04.nothingcamera.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.hicka04.nothingcamera.R

/**
 * カメラ権限が許可されていないことを伝え、再度許可するための導線を表示する。
 *
 * まだ許可の余地がある（[CameraPermissionStatus.ShouldShowRationale]）場合は
 * 権限リクエストダイアログを再表示するボタンを、永久に拒否されている
 * （[CameraPermissionStatus.Denied]）場合はアプリの設定画面を開くボタンを表示する。
 */
@Composable
fun CameraPermissionDeniedContent(
    permissionState: CameraPermissionState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val message = if (permissionState.status == CameraPermissionStatus.ShouldShowRationale) {
            stringResource(R.string.camera_permission_rationale)
        } else {
            stringResource(R.string.camera_permission_denied)
        }
        Text(text = message)
        Spacer(modifier = Modifier.height(16.dp))
        if (permissionState.status == CameraPermissionStatus.ShouldShowRationale) {
            Button(onClick = { permissionState.request() }) {
                Text(stringResource(R.string.camera_permission_request))
            }
        } else {
            Button(onClick = { permissionState.openAppSettings() }) {
                Text(stringResource(R.string.camera_permission_open_settings))
            }
        }
    }
}
