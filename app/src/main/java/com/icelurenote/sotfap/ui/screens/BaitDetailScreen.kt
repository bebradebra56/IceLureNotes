package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.BaitType
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.components.ResultBadge
import com.icelurenote.sotfap.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaitDetailScreen(
    baitType: BaitType,
    entries: List<FishingEntry>,
    onBack: () -> Unit,
    onEntryClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(baitType.displayName) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${entries.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = IceBlue
                            )
                            Text(
                                text = "Total Uses",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightText
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val goodCount = entries.count { it.result == com.icelurenote.sotfap.data.model.Result.GOOD }
                            Text(
                                text = "$goodCount",
                                style = MaterialTheme.typography.headlineMedium,
                                color = SuccessGreen
                            )
                            Text(
                                text = "Good Results",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightText
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "All Entries",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(entries) { entry ->
                BaitEntryCard(entry = entry, onClick = { onEntryClick(entry.id) })
            }
        }
    }
}

@Composable
private fun BaitEntryCard(
    entry: FishingEntry,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }
    
    IceLureCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entry.baitColor.displayName} • ${entry.targetFish.displayName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Depth: ${entry.depth}m • ${dateFormat.format(Date(entry.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightText
                )
            }
            
            ResultBadge(result = entry.result)
        }
    }
}

