package com.carlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.viewmodel.MediaState
import com.carlauncher.ui.theme.*

@Composable
fun MediaBar(
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrev: () -> Unit = {},
) {
    val hasMedia = mediaState.packageName != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDim, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f),
        ) {
            PrevButton { onPrev() }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (hasMedia) BlueAccent.copy(alpha = 0.2f) else SurfaceDim)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (mediaState.isPlaying) "\u23F8" else "\u25B6",
                    fontSize = 18.sp,
                    color = if (hasMedia) TextPrimary else TextMuted,
                )
            }

            NextButton { onNext() }

            if (hasMedia) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = mediaState.title ?: "Unknown",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    mediaState.artist?.let { artist ->
                        Text(
                            text = artist,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            } else {
                Text(
                    text = "No media playing",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PrevButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text("\u23EE", fontSize = 16.sp, color = TextSecondary)
    }
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text("\u23ED", fontSize = 16.sp, color = TextSecondary)
    }
}
