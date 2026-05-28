package com.carlauncher.ui.components

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusBar(
    gpsStrength: kotlinx.coroutines.flow.StateFlow<Int>,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf("") }
    val gpsBars by gpsStrength.collectAsState()
    val context = LocalContext.current
    var btOn by remember { mutableStateOf(checkBt()) }
    var wifiOn by remember { mutableStateOf(checkWifi(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(30_000L)
        }
    }

    val gpsIcon = when (gpsBars) {
        4 -> "\u25A3" ; 3 -> "\u25A2" ; else -> "\u25A1"
    }
    val gpsColor = when (gpsBars) {
        4 -> GreenAccent ; 3 -> TextSecondary ; else -> TextMuted
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = currentTime.ifEmpty {
                SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
            },
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = gpsIcon, color = gpsColor, fontSize = 12.sp)

            Text(
                text = if (btOn) "\uD83D\uDCBB" else "\uD83D\uDCBB",
                color = if (btOn) BlueAccent else TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    toggleBt(context)
                    btOn = checkBt()
                },
            )

            Text(
                text = if (wifiOn) "\uD83D\uDCF6" else "\uD83D\uDCF6",
                color = if (wifiOn) BlueAccent else TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    toggleWifi(context)
                    wifiOn = checkWifi(context)
                },
            )

            Text(text = "75\u00B0F", color = TextMuted, fontSize = 11.sp)
            Text(text = "\u2582\u2584\u2586\u2588", color = TextSecondary, fontSize = 10.sp)
            Text(text = "\uD83D\uDD0B", fontSize = 12.sp)
        }
    }
}

private fun checkBt(): Boolean {
    return try {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter?.isEnabled == true
    } catch (_: Exception) { false }
}

private fun checkWifi(context: Context): Boolean {
    return try {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        wifi?.isWifiEnabled == true
    } catch (_: Exception) { false }
}

private fun toggleBt(context: Context) {
    try {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null) {
            if (adapter.isEnabled) adapter.disable() else adapter.enable()
        }
    } catch (_: Exception) {
        context.startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

private fun toggleWifi(context: Context) {
    try {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        if (wifi != null) {
            wifi.isWifiEnabled = !wifi.isWifiEnabled
        }
    } catch (_: Exception) {
        context.startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
