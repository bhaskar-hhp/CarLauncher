package com.carlauncher.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationState(
    val lat: Double = 37.7749,
    val lng: Double = -122.4194,
    val accuracy: Float = 0f,
    val hasFix: Boolean = false,
)

data class MediaState(
    val title: String = "",
    val artist: String = "",
    val albumArt: Bitmap? = null,
    val isPlaying: Boolean = false,
    val packageName: String? = null,
    val hasSession: Boolean = false,
)

class CarLauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState: StateFlow<MediaState> = _mediaState.asStateFlow()

    private val _gpsStrength = MutableStateFlow(0)
    val gpsStrength: StateFlow<Int> = _gpsStrength.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSessionComponentName: ComponentName? = null
    private var activeController: MediaController? = null
    private val controllers = mutableListOf<MediaController>()

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { sessions ->
        controllers.forEach { it.unregisterCallback(controllerCallback) }
        controllers.clear()
        sessions?.let { list ->
            controllers.addAll(list)
            controllers.forEach { it.registerCallback(controllerCallback) }
            updateFromBestController()
        }
    }

    private lateinit var controllerCallback: MediaController.Callback

    init {
        controllerCallback = object : MediaController.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadata?) {
                updateMediaState(metadata)
            }
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                _mediaState.value = _mediaState.value.copy(
                    isPlaying = state?.state == PlaybackState.STATE_PLAYING,
                )
            }
            override fun onSessionDestroyed() {
                mediaSessionComponentName?.let { cn ->
                    mediaSessionManager?.getActiveSessions(cn)?.let { sessions ->
                        controllers.forEach { it.unregisterCallback(controllerCallback) }
                        controllers.clear()
                        controllers.addAll(sessions)
                        controllers.forEach { it.registerCallback(controllerCallback) }
                        updateFromBestController()
                    }
                }
            }
        }
    }

    fun startLocationUpdates(context: Context) {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000L
            ).apply {
                setMinUpdateIntervalMillis(3000L)
                setMaxUpdateDelayMillis(10000L)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { loc ->
                        _locationState.value = LocationState(
                            lat = loc.latitude,
                            lng = loc.longitude,
                            accuracy = loc.accuracy,
                            hasFix = true,
                        )
                        _gpsStrength.value = if (loc.accuracy < 10) 4
                            else if (loc.accuracy < 25) 3
                            else if (loc.accuracy < 50) 2
                            else 1
                    }
                }
            }

            fusedLocationClient?.requestLocationUpdates(request, locationCallback!!, null)
        } catch (e: SecurityException) {
            Log.w("CarLauncher", "Location permission not granted")
        } catch (e: Exception) {
            Log.e("CarLauncher", "Location service unavailable", e)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
        locationCallback = null
        fusedLocationClient = null
    }

    fun startMediaTracking(context: Context) {
        try {
            val cn = ComponentName(context, com.carlauncher.service.MediaNotificationListener::class.java)
            mediaSessionComponentName = cn
            mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
            mediaSessionManager?.addOnActiveSessionsChangedListener(sessionListener, cn)
            mediaSessionManager?.getActiveSessions(cn)?.let { sessions ->
                controllers.addAll(sessions)
                controllers.forEach { it.registerCallback(controllerCallback) }
                updateFromBestController()
            }
        } catch (e: Exception) {
            Log.e("CarLauncher", "Media session unavailable", e)
        }
    }

    fun stopMediaTracking() {
        mediaSessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
        controllers.forEach { it.unregisterCallback(controllerCallback) }
        controllers.clear()
        activeController = null
        mediaSessionManager = null
        mediaSessionComponentName = null
    }

    private fun updateFromBestController() {
        val ctrl = controllers.firstOrNull { it.playbackState != null }
            ?: controllers.firstOrNull()
        activeController = ctrl
        if (ctrl != null) {
            updateMediaState(ctrl.metadata)
            _mediaState.value = _mediaState.value.copy(
                isPlaying = ctrl.playbackState?.state == PlaybackState.STATE_PLAYING,
                hasSession = true,
                packageName = ctrl.packageName,
            )
        } else {
            _mediaState.value = MediaState(hasSession = false, packageName = null)
        }
    }

    private fun updateMediaState(metadata: MediaMetadata?) {
        if (metadata == null) return
        _mediaState.value = _mediaState.value.copy(
            title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown",
            artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown",
            albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
        )
    }

    fun playPause() {
        if (activeController != null) {
            val tc = activeController!!.transportControls
            val state = _mediaState.value.isPlaying
            if (state) tc.pause() else tc.play()
        } else {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        }
    }

    fun skipNext() {
        if (activeController != null) {
            activeController!!.transportControls.skipToNext()
        } else {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
        }
    }

    fun skipPrevious() {
        if (activeController != null) {
            activeController!!.transportControls.skipToPrevious()
        } else {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        }
    }

    private fun dispatchMediaKey(keyCode: Int) {
        try {
            val am = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        } catch (_: Exception) { }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
        stopMediaTracking()
    }
}
