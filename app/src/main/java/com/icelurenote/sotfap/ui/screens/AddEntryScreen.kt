package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.*
import com.icelurenote.sotfap.ui.components.ChipButton
import com.icelurenote.sotfap.ui.components.PrimaryButton
import com.icelurenote.sotfap.ui.viewmodel.AddEntryState
import com.icelurenote.sotfap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEntryScreen(
    state: AddEntryState,
    unitPreference: UnitPreference,
    onBaitTypeChange: (BaitType) -> Unit,
    onBaitColorChange: (BaitColor) -> Unit,
    onTargetFishChange: (FishType) -> Unit,
    onDepthChange: (String) -> Unit,
    onResultChange: (Result) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val depthUnit = when (unitPreference) {
        UnitPreference.METERS -> "meters"
        UnitPreference.FEET -> "feet"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Entry") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Bait Type",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BaitType.entries.forEach { type ->
                            ChipButton(
                                text = type.displayName,
                                selected = state.baitType == type,
                                onClick = { onBaitTypeChange(type) }
                            )
                        }
                    }
                }
            }
            
            item {
                Column {
                    Text(
                        text = "Bait Color",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BaitColor.entries.forEach { color ->
                            ChipButton(
                                text = color.displayName,
                                selected = state.baitColor == color,
                                onClick = { onBaitColorChange(color) }
                            )
                        }
                    }
                }
            }
            
            item {
                Column {
                    Text(
                        text = "Target Fish",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FishType.entries.forEach { fish ->
                            ChipButton(
                                text = fish.displayName,
                                selected = state.targetFish == fish,
                                onClick = { onTargetFishChange(fish) }
                            )
                        }
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = state.depth,
                    onValueChange = onDepthChange,
                    label = { Text("Depth ($depthUnit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                Column {
                    Text(
                        text = "Result",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Result.entries.forEach { result ->
                            ChipButton(
                                text = result.displayName,
                                selected = state.result == result,
                                onClick = { onResultChange(result) }
                            )
                        }
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notes (ice, weather, activity)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }
            
            item {
                PrimaryButton(
                    text = "Save Entry",
                    onClick = onSave,
                    enabled = state.depth.toFloatOrNull()?.let { it > 0 } == true
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable androidx.compose.foundation.layout.FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

