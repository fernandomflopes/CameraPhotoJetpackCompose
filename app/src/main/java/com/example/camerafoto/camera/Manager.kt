package com.example.camerafoto.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.camerafoto.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Greetings to:
 *      https://www.kiloloco.com/articles/015-camera-jetpack-compose/
 */
open class Manager(private val ctx: Context) {
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    open fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false
    }

    open fun handlerErrorCapture(ex: ImageCaptureException) {

    }

    open fun outputDirectory(): File {
        val mediaDir = ctx.externalMediaDirs.firstOrNull()?.let {
            File(it, ctx.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else ctx.filesDir
    }

    private fun requestCameraPermission(activity: ComponentActivity) {
        when {
            ContextCompat.checkSelfPermission(
                this.ctx,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            ) -> Log.i("myapp", "Show camera permissions dialog")

            else -> requestPermissionLauncher(activity).launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestPermissionLauncher(act: ComponentActivity): ActivityResultLauncher<String> {
        return act.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            shouldShowCamera.value = isGranted
        }
    }

    fun onCreate(activity: ComponentActivity) {
        requestCameraPermission(activity)
    }

    fun onDestroy() {
        cameraExecutor.shutdown()
    }
}

fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri)
        }
    })
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}



