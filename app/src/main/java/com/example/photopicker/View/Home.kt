package com.example.photopicker.View

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photopicker.Util.DispatchGroup
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


@Composable
fun Home(){
    val context = LocalContext.current

    var imageUris: MutableList<Uri> by remember{mutableStateOf(mutableListOf())}

    var storageReferences: MutableList<StorageReference> by remember{ mutableStateOf(mutableListOf()) }


    LaunchedEffect(key1 = Unit){
        val storage = Firebase.storage
        val listRef = storage.reference.child("images")
        Log.d("DEBUG", "started: ${imageUris.toString()}")
//        val ref = listRef.child("6c57bd41-f932-436d-98da-fa5d75879184.jpg")
//        var tmpUris: MutableList<Uri> = mutableListOf()
//        ref.downloadUrl.addOnSuccessListener { it ->
//            //imageUris.add(it)
//            tmpUris.add(it)
//            imageUris = tmpUris
//            Log.d("DEBUG", "done: ${imageUris.toString()}")
//        }

        val dispatchGroup = DispatchGroup()

        var tmpUris: MutableList<Uri> = mutableListOf()
        listRef.listAll()
            .addOnSuccessListener{results ->
                results.items.forEach { res ->
                    dispatchGroup.enter()
                    res.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.d("DEBUG", "ok")
                            //imageUris.add(uri)
                            tmpUris.add(uri)
                            dispatchGroup.leave()
                        }
                        .addOnFailureListener {
                            Log.d("DEBUG", "failed")
                        }

                    dispatchGroup.notify {
                        Log.d("DEBUG", "notified!!")
                        imageUris = tmpUris
                        Log.d("HERE", imageUris.toString())

                    }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
                Log.d("DEBUG" , "Error occured")
            }
    }


    Column {
        Text("Uploaded Image")

        LazyColumn{
            items(imageUris){ uri ->
                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(200.dp))

            }

        }

    }



}


