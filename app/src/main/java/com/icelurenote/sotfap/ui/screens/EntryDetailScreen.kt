package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.data.model.UnitPreference
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.components.ResultBadge
import com.icelurenote.sotfap.ui.theme.*
import com.icelurenote.sotfap.utils.DepthConverter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entry: FishingEntry?,
    unitPreference: UnitPreference,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (entry != null) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        }
    ) { padding ->
        if (entry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Entry not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val dateFormat = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' HH:mm", Locale.ENGLISH) }
                
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                        Text(
                            text = dateFormat.format(Date(entry.timestamp)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkText
                        )
                    }
                }
                
                IceLureCard {
                    DetailRow("Bait Type", entry.baitType.displayName)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Bait Color", entry.baitColor.displayName)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Target Fish", entry.targetFish.displayName)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Depth", DepthConverter.formatDepth(entry.depth, unitPreference))
                }
                
                IceLureCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Result",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                        ResultBadge(result = entry.result)
                    }
                }
                
                if (entry.notes.isNotBlank()) {
                    IceLureCard {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = entry.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText
                        )
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this entry? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText
        )
    }
}

