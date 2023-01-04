package com.example.camerafoto.camera.ui

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.camerafoto.camera.Manager
import com.example.camerafoto.camera.getCameraProvider
import com.example.camerafoto.camera.takePhoto
import java.io.File
import java.util.concurrent.Executor

@Composable
fun BaseCameraView(
    content: @Composable (imageCapture: ImageCapture) -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        content(imageCapture)
    }

}

@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    BaseCameraView { imageCapture ->
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            IconButton(
                modifier = Modifier
                       .padding(bottom = 20.dp),
                onClick = {
                      takePhoto(
                      filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                      imageCapture = imageCapture,
                      outputDirectory = outputDirectory,
                      executor = executor,
                      onImageCaptured = onImageCaptured,
                      onError = onError
                      ) },
                content = {
                      Icon(
                          imageVector = Icons.Sharp.PlayArrow,
                          contentDescription = "Take picture",
                          tint = Color.White,
                          modifier = Modifier
                          .size(100.dp)
                          .padding(1.dp)
                          .border(1.dp, Color.White, CircleShape)
                      )
                      }
            )

        }
    }

}

@Composable
fun CameraView(cameraManager: Manager) {
    CameraView(
        outputDirectory = cameraManager.outputDirectory(),
        executor = cameraManager.cameraExecutor,
        onImageCaptured = cameraManager::handleImageCapture,
        onError = cameraManager::handlerErrorCapture
    )
}