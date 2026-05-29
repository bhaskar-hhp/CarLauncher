package com.carlauncher.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.carlauncher.ui.theme.*
import com.carlauncher.viewmodel.LocationState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapPanel(
    locationState: LocationState,
    modifier: Modifier = Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var webViewReady by remember { mutableStateOf(false) }
    var isNightMode by remember { mutableStateOf(true) }
    var trafficOn by remember { mutableStateOf(false) }

    LaunchedEffect(locationState.lat, locationState.lng, webViewReady) {
        if (webViewReady && locationState.hasFix) {
            webView?.evaluateJavascript(
                "updateLocation(${locationState.lat}, ${locationState.lng})",
                null,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkCard),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    setWebViewClient(object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            webViewReady = true
                            if (locationState.hasFix) {
                                view?.evaluateJavascript(
                                    "updateLocation(${locationState.lat}, ${locationState.lng})",
                                    null,
                                )
                            }
                        }
                    })
                    loadUrl("file:///android_asset/map.html")
                    webView = this
                }
            },
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MapToggleButton(
                icon = if (isNightMode) "\u2600\uFE0F" else "\uD83C\uDF19",
                label = if (isNightMode) "Day" else "Night",
                active = isNightMode,
                onClick = {
                    isNightMode = !isNightMode
                    webView?.evaluateJavascript("setNightMode($isNightMode)", null)
                },
            )
            MapToggleButton(
                icon = "\uD83D\uDEA7",
                label = "Traffic",
                active = trafficOn,
                onClick = {
                    trafficOn = !trafficOn
                    webView?.evaluateJavascript("toggleTraffic()", null)
                },
            )
        }
    }
}

@Composable
private fun MapToggleButton(
    icon: String,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) BlueAccent.copy(alpha = 0.25f) else Color(0x88000000))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = icon, fontSize = 11.sp)
            Text(
                text = label,
                color = if (active) BlueAccent else TextMuted,
                fontSize = 10.sp,
            )
        }
    }
}
