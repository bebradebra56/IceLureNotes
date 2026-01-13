package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.ui.components.StatCard
import com.icelurenote.sotfap.ui.theme.*
import com.icelurenote.sotfap.ui.viewmodel.BaitStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    totalEntries: Int,
    baitStatistics: List<BaitStats>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "General Stats",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Entries",
                        value = "$totalEntries",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Baits Tested",
                        value = "${baitStatistics.size}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            if (baitStatistics.isNotEmpty()) {
                item {
                    val topBait = baitStatistics.firstOrNull()
                    StatCard(
                        title = "Top Performer",
                        value = topBait?.baitType?.displayName ?: "N/A",
                        subtitle = topBait?.let { "⭐ ${String.format("%.1f", it.averageResult)}" }
                    )
                }
                
                item {
                    Text(
                        text = "Bait Performance",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                items(baitStatistics.size) { index ->
                    BaitPerformanceBar(
                        baitName = baitStatistics[index].baitType.displayName,
                        usageCount = baitStatistics[index].usageCount,
                        rating = baitStatistics[index].averageResult,
                        maxUsage = baitStatistics.maxOf { it.usageCount }
                    )
                }
            }
        }
    }
}

@Composable
private fun BaitPerformanceBar(
    baitName: String,
    usageCount: Int,
    rating: Double,
    maxUsage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = baitName,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkText
                )
                Text(
                    text = "⭐ ${String.format("%.1f", rating)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IceBlue
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { usageCount.toFloat() / maxUsage.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    rating >= 2.5 -> SuccessGreen
                    rating >= 2.0 -> WarningAmber
                    else -> DangerRed
                },
                trackColor = IceShadow
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$usageCount uses",
                style = MaterialTheme.typography.bodySmall,
                color = LightText
            )
        }
    }
}

