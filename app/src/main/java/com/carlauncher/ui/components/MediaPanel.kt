package com.carlauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        Column(modifier = Modifier.padding(14.dp, 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF212121)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\uD83C\uDFB5", fontSize = 17.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaState.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(text = mediaState.artist, color = TextTertiary, fontSize = 10.sp)
                }
                Text(
                    text = "YT Music",
                    color = Color(0xFFFF0000),
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(color = MediaProgressTrack, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f, 1f))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(1.dp)),
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFF0000), Color(0xFFFF4444))),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f, 1f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\u23EE",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSkipPrevious() },
                )
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF0000))
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center,
                ) {
                    val playIcon = if (mediaState.isPlaying) "\u23F8" else "\u25B6"
                    Text(text = playIcon, color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "\u23ED",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSkipNext() },
                )
            }
        }
    }
}
