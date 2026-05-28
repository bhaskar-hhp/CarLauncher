package com.carlauncher.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.ui.theme.TextMuted
import com.carlauncher.ui.theme.TextSecondary
import com.carlauncher.ui.theme.GreenAccent
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusBar(
    gpsStrength: kotlinx.coroutines.flow.StateFlow<Int>,
    modifier: Modifier = Modifier,
) {
    var currentTime by remember { mutableStateOf("") }
    val gpsBars by gpsStrength.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(30_000L)
        }
    }

    val gpsIcon = when (gpsBars) {
        4 -> "\u25A3"
        3 -> "\u25A2"
        2 -> "\u25A1"
        else -> "\u25A1"
    }
    val gpsColor = when (gpsBars) {
        4 -> GreenAccent
        3 -> TextSecondary
        else -> TextMuted
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = currentTime.ifEmpty {
                    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
                },
                color = TextSecondary,
                fontSize = 13.sp,
            )
            Text(text = "\u25CF\u25CF\u25CF\u25CF\u25CB", color = TextMuted, fontSize = 11.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = gpsIcon, color = gpsColor, fontSize = 12.sp)
            Text(text = "75\u00B0F", color = TextMuted, fontSize = 11.sp)
            Text(text = "\u2582\u2584\u2586\u2588", color = TextSecondary, fontSize = 10.sp)
            Text(text = "WiFi", color = TextSecondary, fontSize = 10.sp)
            Text(text = "\uD83D\uDD0B", fontSize = 12.sp)
        }
    }
}
