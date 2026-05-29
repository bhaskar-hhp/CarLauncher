package com.carlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.carlauncher.viewmodel.MediaState
import com.carlauncher.ui.theme.*
import com.carlauncher.R

@Composable
fun MediaBar(
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrev: () -> Unit = {},
) {
    val hasMedia = mediaState.packageName != null
    val bgColor = if (hasMedia) BlueAccent.copy(alpha = 0.2f) else SurfaceDim

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MediaButton(
            icon = R.drawable.ic_skip_prev,
            contentDesc = "Previous",
            enabled = hasMedia,
            onClick = onPrev,
        )

        Spacer(Modifier.width(20.dp))

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(enabled = hasMedia) { onPlayPause() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(
                    if (mediaState.isPlaying) R.drawable.ic_pause
                    else R.drawable.ic_play
                ),
                contentDescription = if (mediaState.isPlaying) "Pause" else "Play",
                tint = if (hasMedia) TextPrimary else TextMuted,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(Modifier.width(20.dp))

        MediaButton(
            icon = R.drawable.ic_skip_next,
            contentDesc = "Next",
            enabled = hasMedia,
            onClick = onNext,
        )
    }
}

@Composable
private fun MediaButton(
    icon: Int,
    contentDesc: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDesc,
            tint = if (enabled) TextSecondary else TextMuted.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp),
        )
    }
}
