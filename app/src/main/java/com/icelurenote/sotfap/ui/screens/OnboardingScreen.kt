package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icelurenote.sotfap.ui.components.PrimaryButton
import com.icelurenote.sotfap.ui.components.SecondaryButton
import com.icelurenote.sotfap.ui.theme.BackgroundLight
import com.icelurenote.sotfap.ui.theme.IceBlue
import com.icelurenote.sotfap.ui.theme.IceShadow
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: String,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = "🎣",
        title = "Track Winter Bait Usage",
        description = "Record every bait you use during your ice fishing trips and never forget what worked."
    ),
    OnboardingPage(
        icon = "📊",
        title = "Log Conditions and Results",
        description = "Document depth, weather, ice conditions, and catch results for each session."
    ),
    OnboardingPage(
        icon = "✨",
        title = "Find What Works Best",
        description = "Analyze your data to discover which baits and conditions produce the best results."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(onboardingPages[page])
            }
            
            // Page Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardingPages.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width = if (pagerState.currentPage == index) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .background(
                                color = if (pagerState.currentPage == index) IceBlue else IceShadow,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
            }
            
            // Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (pagerState.currentPage == onboardingPages.size - 1) {
                    PrimaryButton(
                        text = "Get Started",
                        onClick = onComplete
                    )
                } else {
                    PrimaryButton(
                        text = "Next",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = "Skip",
                        onClick = onComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = page.icon,
            fontSize = 96.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

