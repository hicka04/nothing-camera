package dev.hicka04.nothingcamera.preview

import android.content.Context
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * カメラのプレビュー映像を扱う ViewModel。
 *
 * [Preview] ユースケースの [SurfaceRequest] を [StateFlow] として公開し、
 * [bindToCamera] が呼ばれている間だけ背面カメラをライフサイクルにバインドし続ける。
 */
class CameraPreviewViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    /**
     * 背面カメラを [lifecycleOwner] にバインドする。
     *
     * 呼び出し元がキャンセルされるまで（Composable が破棄されるまで）バインドし続け、
     * キャンセル時に [ProcessCameraProvider.unbindAll] で解放する。
     * バインド自体は [lifecycleOwner] の状態にも従うため、バックグラウンド遷移時は
     * 自動的に unbind され、フォアグラウンド復帰時に自動的に再バインドされる。
     */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            DEFAULT_BACK_CAMERA,
            previewUseCase,
        )

        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}
