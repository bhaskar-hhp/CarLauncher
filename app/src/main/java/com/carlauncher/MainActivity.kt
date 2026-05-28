package com.carlauncher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlauncher.ui.components.*
import com.carlauncher.ui.theme.*
import com.carlauncher.viewmodel.CarLauncherViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CarLauncherViewModel
    private var locationGranted = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationGranted = permissions.values.any { it }
        if (locationGranted) {
            viewModel.startLocationUpdates(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            viewModel = viewModel()
            val locationState by viewModel.locationState.collectAsState()
            val mediaState by viewModel.mediaState.collectAsState()

            CarLauncherTheme {
                CarLauncherScreen(
                    locationState = locationState,
                    mediaState = mediaState,
                    onPlayPause = { viewModel.playPause() },
                    onSkipNext = { viewModel.skipNext() },
                    onSkipPrevious = { viewModel.skipPrevious() },
                    onLaunchApp = { app ->
                        launchApp(app.packageName)
                    },
                    gpsStrength = viewModel.gpsStrength,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            locationGranted = fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
            if (locationGranted) {
                viewModel.startLocationUpdates(this)
            } else {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("CarLauncher", "Location init failed", e)
        }
        try {
            viewModel.startMediaTracking(this)
        } catch (e: Exception) {
            Log.e("CarLauncher", "Media tracking init failed", e)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopLocationUpdates()
        viewModel.stopMediaTracking()
    }

    private fun launchApp(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=$packageName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(marketIntent)
            }
        } catch (_: Exception) { }
    }
}

@Composable
private fun CarLauncherScreen(
    locationState: com.carlauncher.viewmodel.LocationState,
    mediaState: com.carlauncher.viewmodel.MediaState,
    onPlayPause: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onLaunchApp: (DockApp) -> Unit = {},
    gpsStrength: kotlinx.coroutines.flow.StateFlow<Int>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 28.dp),
    ) {
        StatusBar(gpsStrength = gpsStrength)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MediaBar(
                    mediaState = mediaState,
                    onPlayPause = onPlayPause,
                    onNext = onSkipNext,
                    onPrev = onSkipPrevious,
                )

                AppShortcuts(
                    slots = List(4) { ShortcutSlot() },
                    onSlotClick = { index ->
                        /* TODO: app picker */
                    },
                )

                Spacer(Modifier.weight(1f))

                AppDock(onAppClick = onLaunchApp)
            }

            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
            ) {
                MapPanel(locationState = locationState)
            }
        }
    }
}
