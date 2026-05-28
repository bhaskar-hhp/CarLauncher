package com.carlauncher.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .padding(end = 24.dp, bottom = 4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xAA000000), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Column {
                    Text(text = "ARRIVAL", color = TextTertiary, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text(text = "12 min", color = BlueAccent, fontSize = 20.sp, letterSpacing = 0.sp)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = DarkBorder)
                    }
                }
                Column {
                    Text(text = "DISTANCE", color = TextTertiary, fontSize = 10.sp, letterSpacing = 1.sp)
                    val distText = if (locationState.hasFix) "3.2 mi" else "—"
                    Text(text = distText, color = TextPrimary, fontSize = 16.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .height(36.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0x99000000), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "\uD83D\uDD0D  Search destination\u2026",
                    color = TextMuted,
                    fontSize = 12.sp,
                )
            }
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .fillMaxHeight()
                    .background(Color(0x88000000), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\uD83C\uDFA4", fontSize = 13.sp)
            }
        }
    }
}
