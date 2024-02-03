package com.example.traveltribe.ui.components.routes

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.tasks.await

@Composable
fun Routes(navController: NavController) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                content = {
                    Icon(Icons.Filled.LocationOn,"")
                }
            )
        }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(it),
            factory = { mapView },
        )
    }

    DisposableEffect(context, lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> MapKitFactory.initialize(context)
                Lifecycle.Event.ON_START -> {
                    MapKitFactory.getInstance().onStart()
                    mapView.onStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    MapKitFactory.getInstance().onStop()
                    mapView.onStop()
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

//@Composable
//fun requestLocationsPermissions() {
//    val context = LocalContext.current
//    val launcherMultiplePermissions = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissionsMap ->
//        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
//        if (areGranted) {
//            locationRequired = true
//            startLocationUpdates()
//            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
//@SuppressLint("MissingPermission")
//suspend fun getLocation(context: Context): Location? {
//    val locationClient = LocationServices.getFusedLocationProviderClient(context)
//    val r = LocationRequest.Builder,
//    val request = CurrentLocationRequest.Builder()
//        .setDurationMillis(10000)
//        .setMaxUpdateAgeMillis()
//        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//        .build()
//    val locationRequest = LocationRequest.create().apply {
//        interval = 10000
//        fastestInterval = 5000
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//   return locationClient.getCurrentLocation(
//        Priority.PRIORITY_HIGH_ACCURACY,
//        CancellationTokenSource().token,
//    ).await()
//}
