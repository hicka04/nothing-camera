package dev.hicka04.nothingcamera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

/** カメラ権限の状態。 */
enum class CameraPermissionStatus {
    /** 許可されている。 */
    Granted,

    /** 拒否されているが、再度リクエストすれば OS のダイアログが表示される。 */
    ShouldShowRationale,

    /** 「今後表示しない」等で拒否されており、アプリの設定画面からしか許可できない。 */
    Denied,
}

/** カメラ権限の状態を保持し、リクエストや設定画面遷移の操作を提供する。 */
class CameraPermissionState(
    status: CameraPermissionStatus,
    private val launchPermissionRequest: () -> Unit,
    private val context: Context,
) {
    var status by mutableStateOf(status)
        internal set

    /** カメラ権限のリクエストダイアログを表示する。 */
    fun request() {
        launchPermissionRequest()
    }

    /** アプリの設定画面を開く。 */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}

/**
 * カメラ権限の状態を管理する [CameraPermissionState] を返す。
 *
 * 権限リクエストの結果、およびアプリが再開されるたび（設定画面からの復帰などを含む）に
 * 状態を再評価する。
 */
@Composable
fun rememberCameraPermissionState(): CameraPermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current

    fun currentStatus(): CameraPermissionStatus = when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED -> CameraPermissionStatus.Granted

        activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA,
            ) -> CameraPermissionStatus.ShouldShowRationale

        else -> CameraPermissionStatus.Denied
    }

    var status by remember { mutableStateOf(currentStatus()) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { status = currentStatus() }

    LifecycleResumeEffect(Unit) {
        status = currentStatus()
        onPauseOrDispose { }
    }

    val permissionState = remember {
        CameraPermissionState(
            status = status,
            launchPermissionRequest = { launcher.launch(Manifest.permission.CAMERA) },
            context = context,
        )
    }
    permissionState.status = status

    return permissionState
}
