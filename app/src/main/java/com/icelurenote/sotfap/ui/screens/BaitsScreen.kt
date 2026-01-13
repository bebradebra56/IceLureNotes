package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.ui.components.EmptyState
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.theme.*
import com.icelurenote.sotfap.ui.viewmodel.BaitStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaitsScreen(
    baitStatistics: List<BaitStats>,
    onBaitClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Baits Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        }
    ) { padding ->
        if (baitStatistics.isEmpty()) {
            EmptyState(
                message = "No bait data yet.\nStart adding entries!",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(baitStatistics) { stat ->
                    BaitStatCard(
                        stat = stat,
                        onClick = { onBaitClick(stat.baitType.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BaitStatCard(
    stat: BaitStats,
    onClick: () -> Unit
) {
    IceLureCard(onClick = onClick) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.baitType.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText
                )
                Text(
                    text = "⭐ ${String.format("%.1f", stat.averageResult)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = IceBlue
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Times Used",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightText
                    )
                    Text(
                        text = "${stat.usageCount}",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText
                    )
                }
                
                Column {
                    Text(
                        text = "Success Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightText
                    )
                    val successRate = (stat.averageResult / 3.0 * 100).toInt()
                    Text(
                        text = "$successRate%",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (successRate >= 70) SuccessGreen else if (successRate >= 50) WarningAmber else DangerRed
                    )
                }
            }
        }
    }
}

