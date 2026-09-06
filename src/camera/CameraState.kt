package dev.hicka04.nothingcamera.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * CameraX のユースケースの生成からライフサイクルへのバインド、撮影処理までを保持する。
 *
 * [bindToLifecycle] の呼び出し元がライフサイクルへのバインドを担い、
 * それ以外の操作（プレビューの表示、撮影）はこのクラス経由で行う。
 */
class CameraState(private val context: Context) {
    /** [androidx.camera.compose.CameraXViewfinder] に渡すためのプレビュー映像のリクエスト。 */
    var surfaceRequest by mutableStateOf<SurfaceRequest?>(null)
        private set

    /** 撮影処理の実行中かどうか。二重押下の防止に使う。 */
    var isCapturing by mutableStateOf(false)
        private set

    private val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest -> surfaceRequest = newSurfaceRequest }
    }
    private val imageCaptureUseCase = ImageCapture.Builder().build()

    /**
     * カメラのユースケースを [lifecycleOwner] にバインドする。
     *
     * 呼び出し元がキャンセルされる（≒ Composable が破棄される）までバインドを維持し、
     * キャンセル時に unbind する。
     */
    suspend fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context.applicationContext)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            DEFAULT_BACK_CAMERA,
            previewUseCase,
            imageCaptureUseCase,
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    /**
     * 写真を撮影し、JPEG として端末の MediaStore に保存する。
     *
     * 保存先はギャラリーアプリから見える `Pictures/NothingCamera` 配下。
     * 撮影中に呼び出した場合は何もしない。
     */
    suspend fun takePicture() {
        if (isCapturing) return
        isCapturing = true
        try {
            val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/NothingCamera")
            }
            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ).build()

            suspendCancellableCoroutine<Uri?> { continuation ->
                imageCaptureUseCase.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            continuation.resume(outputFileResults.savedUri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            continuation.resumeWithException(exception)
                        }
                    },
                )
            }
        } finally {
            isCapturing = false
        }
    }
}

/**
 * [CameraState] を生成し、現在の [LocalLifecycleOwner] にバインドされた状態で返す。
 *
 * バインドは [LocalLifecycleOwner] に従うため、バックグラウンド遷移時は自動的に unbind され、
 * フォアグラウンド復帰時に自動的に再バインドされる。
 */
@Composable
fun rememberCameraState(): CameraState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraState = remember { CameraState(context.applicationContext) }

    LaunchedEffect(lifecycleOwner) {
        cameraState.bindToLifecycle(lifecycleOwner)
    }

    return cameraState
}
