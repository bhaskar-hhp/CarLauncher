package com.carlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.ui.theme.*

data class QuickTile(val icon: String, val label: String, val extra: String? = null, val active: Boolean = false)

@Composable
fun QuickTiles(tiles: List<QuickTile>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tiles.forEach { tile ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (tile.active) BlueAccent.copy(alpha = 0.12f) else DarkBorder.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = if (tile.active) BlueAccent.copy(alpha = 0.2f) else DarkBorder.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(text = tile.icon, fontSize = 14.sp)
                    Text(
                        text = tile.extra ?: tile.label,
                        color = if (tile.active) TextPrimary else TextSecondary,
                        fontSize = if (tile.extra != null) 13.sp else 11.sp,
                    )
                    if (tile.extra == null) {
                        Text(text = tile.label, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
