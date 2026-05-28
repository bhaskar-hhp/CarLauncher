package com.carlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.ui.theme.*

data class DockApp(val name: String, val icon: String, val bgColor: Color, val packageName: String)

val defaultDockApps = listOf(
    DockApp("Phone", "📞", GreenAccent, "com.android.dialer"),
    DockApp("Music", "🎵", PurpleAccent, "com.android.music"),
    DockApp("Maps", "🗺️", BlueAccent, "com.google.android.apps.maps"),
    DockApp("Settings", "⚙️", DarkBorder.copy(alpha = 0.5f), "com.android.settings"),
)

@Composable
fun AppDock(
    apps: List<DockApp> = defaultDockApps,
    onAppClick: (DockApp) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        apps.forEach { app ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.clickable { onAppClick(app) },
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(app.bgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = app.icon, fontSize = 18.sp)
                }
                Text(text = app.name, color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}
