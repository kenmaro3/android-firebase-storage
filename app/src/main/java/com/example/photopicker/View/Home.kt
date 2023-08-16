package com.example.photopicker.View

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photopicker.Util.DispatchGroup
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


@Composable
fun Home(){
    var imageUris: MutableList<Uri> by remember{mutableStateOf(mutableListOf())}

    LaunchedEffect(key1 = Unit){
        val storage = Firebase.storage
        val listRef = storage.reference.child("images")

        val dispatchGroup = DispatchGroup()

        var tmpUris: MutableList<Uri> = mutableListOf()
        listRef.listAll()
            .addOnSuccessListener{results ->
                results.items.forEach { res ->
                    dispatchGroup.enter()
                    res.downloadUrl
                        .addOnSuccessListener { uri ->
                            tmpUris.add(uri)
                            dispatchGroup.leave()
                        }
                        .addOnFailureListener {

                        }

                    dispatchGroup.notify {
                        imageUris = tmpUris

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


