package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.components.StatCard
import com.icelurenote.sotfap.ui.theme.*
import com.icelurenote.sotfap.ui.viewmodel.ResultsSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    summary: ResultsSummary,
    onStatsClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
                actions = {
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Default.DateRange, "Calendar")
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.Info, "Stats")
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
                    text = "Overview",
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
                        value = "${summary.totalEntries}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Baits Tested",
                        value = "${summary.baitsTested}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                StatCard(
                    title = "Successful Sessions",
                    value = "${summary.successfulSessions}",
                    subtitle = if (summary.totalEntries > 0) {
                        "${(summary.successfulSessions * 100 / summary.totalEntries)}% success rate"
                    } else null
                )
            }
            
            item {
                Text(
                    text = "Best Performers",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Most Effective Bait",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = summary.mostEffectiveBait?.displayName ?: "No data",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkText
                            )
                        }
                        Text(
                            text = "🎣",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
            
            item {
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Best Depth",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = summary.bestDepth?.let { "${it}m" } ?: "No data",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkText
                            )
                        }
                        Text(
                            text = "📏",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
            
            item {
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Most Common Fish",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = summary.mostCommonFish?.displayName ?: "No data",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkText
                            )
                        }
                        Text(
                            text = "🐟",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }
}

