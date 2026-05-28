package com.carlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlauncher.ui.theme.*
import com.carlauncher.viewmodel.MediaState

@Composable
fun MediaPanel(
    mediaState: MediaState,
    onPlayPause: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceVariant, RoundedCornerShape(18.dp)),
    ) {
        Column(modifier = Modifier.padding(16.dp, 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MediaProgress),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\uD83C\uDFB5", fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaState.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(text = mediaState.artist, color = TextTertiary, fontSize = 11.sp)
                }
                Text(text = "\u2661", color = TextMuted, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(color = MediaProgressTrack, cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp)),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(listOf(MediaProgress, PurpleAccent)),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\u23EE",
                    color = TextSecondary,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onSkipPrevious() },
                )
                Spacer(modifier = Modifier.width(24.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkBorder)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center,
                ) {
                    val playIcon = if (mediaState.isPlaying) "\u23F8" else "\u25B6"
                    Text(text = playIcon, color = TextPrimary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "\u23ED",
                    color = TextSecondary,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onSkipNext() },
                )
            }
        }
    }
}
