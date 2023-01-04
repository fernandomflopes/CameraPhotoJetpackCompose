package com.example.camerafoto.camera.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    onRemove: () -> Unit,
    onDone: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {

        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = onRemove
                ) {
                    Icon(Icons.Filled.Delete,"", tint = Color.Red)
                }
                Spacer(modifier = Modifier.size(16.dp))
                FloatingActionButton(
                    onClick = onDone
                ) {
                    Icon(Icons.Filled.Done,"", tint = Color.Magenta)
                }
            }
        }

    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) {
            content()
        }
    }
}
