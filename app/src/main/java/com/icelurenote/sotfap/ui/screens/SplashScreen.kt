package com.icelurenote.sotfap.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icelurenote.sotfap.ui.theme.BackgroundLight
import com.icelurenote.sotfap.ui.theme.IceBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: (Boolean) -> Unit,
    isOnboardingCompleted: Boolean
) {
    var alpha by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        ) { value, _ ->
            alpha = value
        }
        delay(2000)
        onNavigateNext(isOnboardingCompleted)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Text(
                text = "❄️🎣",
                fontSize = 72.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ice Lure Notes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = IceBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Track Your Winter Fishing",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

