package com.example.camerafoto

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.camerafoto.camera.Manager
import com.example.camerafoto.camera.ui.CameraView
import com.example.camerafoto.camera.ui.EditView
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {

    private lateinit var manager : Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            if(manager.shouldShowCamera.value) {
                Column {
                    CameraView(manager)
                }
            } else {
                if(manager.savedUris.size > 0) {
                    manager.savedUris.toList().last().apply {
                        EditView(
                            onRemove = { manager.shouldShowCamera.value = true },
                            onDone = {}
                        ) {
                            Image(
                                alignment = Alignment.TopStart,
                                painter = rememberImagePainter(this),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    Text(text = "")
                }
            }

        }
        manager = Manager(this)
        manager.onCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.onDestroy()
    }
}

