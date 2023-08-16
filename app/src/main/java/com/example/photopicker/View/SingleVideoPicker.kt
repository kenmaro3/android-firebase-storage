package com.example.photopicker.View

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photopicker.Util.StorageUtil
import com.google.firebase.BuildConfig


@Composable
fun SingleVideoPicker(){
    var uri by remember{
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )

    val context = LocalContext.current

    Column(
//        modifier = Modifier
//            .background(androidx.compose.ui.graphics.Color.White)
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
    ){
        Row{
            Button(onClick = {
                singlePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )

            }){
                Text("Pick Video")
            }

            Button(onClick = {
                uri?.let{
                    StorageUtil.uploadToStorage(uri=it, context=context, type="video")
                }

            }){
                Text("Upload")
            }
        }

        val mediaMetadataRetriever = MediaMetadataRetriever()
        var thumbnail: Bitmap? = null
        try {
            mediaMetadataRetriever.setDataSource(context, uri)
            thumbnail = mediaMetadataRetriever.getFrameAtTime(100) // time in Micros
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        } finally {
            mediaMetadataRetriever.release()
        }

        AsyncImage(model = thumbnail, contentDescription = null, modifier = Modifier.size(248.dp))

        uri?.let{
            VideoPlayerScreen(uri = it)
        }

    }
}
