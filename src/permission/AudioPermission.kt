package dev.hicka04.nothingcamera.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** 現在マイクの使用が許可されているかどうかを返す。 */
fun hasAudioPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED

/**
 * マイクの使用許可をリクエストする関数を返す。
 *
 * 権限が確定した（許可・拒否いずれか）タイミングで [onResult] が呼ばれる。
 * すでに許可されている場合や、過去に「今後表示しない」等で拒否されている場合は、
 * OS のダイアログを表示せずに即座に結果が返る。
 */
@Composable
fun rememberRequestAudioPermission(onResult: (granted: Boolean) -> Unit): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult,
    )

    return { launcher.launch(Manifest.permission.RECORD_AUDIO) }
}
