package com.adoejosld.mycamera.home

import android.Manifest
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(onClick = {
            val executor = ContextCompat.getMainExecutor(context)
            takePhoto(cameraController, executor)
        }) {
            Text(text = "Camara")
        }
    }) {
        if (permissionState.status.isGranted) {
            CameraScreen(
                cameraController = cameraController,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.padding(it)
            )
        } else {
            Text(text = "Permission Denied", modifier = Modifier.padding(it))
        }
    }
}

private fun takePhoto(cameraController: LifecycleCameraController, executor: Executor) {
    val fileName = File.createTempFile("image", ".jpg")
    val outputFile = ImageCapture.OutputFileOptions.Builder(fileName).build()
    cameraController.takePicture(/* outputFileOptions = */ outputFile, /* executor = */
        executor, /* imageSavedCallback = */
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                println(message = outputFileResults.savedUri)
            }

            override fun onError(p0: ImageCaptureException) {
            }
        }
    )
}

@Composable
fun CameraScreen(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }
    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        previewView.controller = cameraController
        previewView
    })
}
