package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.FishType
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.data.model.Result
import com.icelurenote.sotfap.ui.components.*
import com.icelurenote.sotfap.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    entries: List<FishingEntry>,
    selectedFishFilter: FishType?,
    selectedResultFilter: Result?,
    onFishFilterChange: (FishType?) -> Unit,
    onResultFilterChange: (Result?) -> Unit,
    onEntryClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Journal") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.MoreVert, "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = IceBlue,
                contentColor = SnowWhite
            ) {
                Icon(Icons.Default.Add, "Add Entry")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyState(
                message = "No entries yet.\nStart tracking your baits!",
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
                items(entries) { entry ->
                    EntryCard(
                        entry = entry,
                        onClick = { onEntryClick(entry.id) }
                    )
                }
            }
        }
    }
    
    if (showFilterSheet) {
        FilterBottomSheet(
            selectedFish = selectedFishFilter,
            selectedResult = selectedResultFilter,
            onFishSelected = onFishFilterChange,
            onResultSelected = onResultFilterChange,
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
private fun EntryCard(
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
                    text = entry.baitType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.baitColor.displayName} • ${entry.targetFish.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = SubtleText
                )
            }
            
            ResultBadge(result = entry.result)
        }
        
        if (entry.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.notes,
                style = MaterialTheme.typography.bodySmall,
                color = LightText,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    selectedFish: FishType?,
    selectedResult: Result?,
    onFishSelected: (FishType?) -> Unit,
    onResultSelected: (Result?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Filter Entries",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Fish Type",
                style = MaterialTheme.typography.titleSmall,
                color = LightText
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChipButton(
                        text = "All",
                        selected = selectedFish == null,
                        onClick = { onFishSelected(null) }
                    )
                }
                items(FishType.entries) { fish ->
                    ChipButton(
                        text = fish.displayName,
                        selected = selectedFish == fish,
                        onClick = { onFishSelected(if (selectedFish == fish) null else fish) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Result",
                style = MaterialTheme.typography.titleSmall,
                color = LightText
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChipButton(
                        text = "All",
                        selected = selectedResult == null,
                        onClick = { onResultSelected(null) }
                    )
                }
                items(Result.entries) { result ->
                    ChipButton(
                        text = result.displayName,
                        selected = selectedResult == result,
                        onClick = { onResultSelected(if (selectedResult == result) null else result) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

