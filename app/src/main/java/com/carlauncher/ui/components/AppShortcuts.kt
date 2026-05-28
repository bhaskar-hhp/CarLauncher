package com.carlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

data class ShortcutSlot(
    val label: String = "",
    val icon: String = "",
    val packageName: String? = null,
    val isEmpty: Boolean = true,
)

@Composable
fun AppShortcuts(
    slots: List<ShortcutSlot> = List(4) { ShortcutSlot() },
    onSlotClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        slots.forEachIndexed { index, slot ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (slot.isEmpty) DarkBorder.copy(alpha = 0.3f) else DarkBorder.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = if (slot.isEmpty) DarkBorder.copy(alpha = 0.15f) else DarkBorder.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable { onSlotClick(index) },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = if (slot.isEmpty) "+" else slot.icon,
                        color = if (slot.isEmpty) TextMuted else TextPrimary,
                        fontSize = if (slot.isEmpty) 22.sp else 18.sp,
                    )
                    if (!slot.isEmpty) {
                        Text(
                            text = slot.label,
                            color = TextMuted,
                            fontSize = 9.sp,
                        )
                    }
                }
            }
        }
    }
}
