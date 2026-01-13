package com.icelurenote.sotfap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.components.ResultBadge
import com.icelurenote.sotfap.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    entries: List<FishingEntry>,
    onBack: () -> Unit,
    onEntryClick: (Long) -> Unit
) {
    var currentMonth by remember { 
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    
    val entriesByDate = remember(entries) {
        entries.groupBy { entry ->
            Calendar.getInstance().apply {
                timeInMillis = entry.timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month selector
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                    }
                },
                onNextMonth = {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }
                }
            )
            
            // Calendar grid
            CalendarGrid(
                currentMonth = currentMonth,
                entriesByDate = entriesByDate,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = if (selectedDate?.timeInMillis == date.timeInMillis) null else date
                }
            )
            
            // Entries for selected date
            selectedDate?.let { date ->
                val dateEntries = entriesByDate[date.timeInMillis] ?: emptyList()
                if (dateEntries.isNotEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                            Text(
                                text = dateFormat.format(date.time),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(dateEntries) { entry ->
                            CalendarEntryCard(entry = entry, onClick = { onEntryClick(entry.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.KeyboardArrowLeft, "Previous month")
        }
        
        Text(
            text = monthFormat.format(currentMonth.time),
            style = MaterialTheme.typography.titleLarge
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.KeyboardArrowRight, "Next month")
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Calendar,
    entriesByDate: Map<Long, List<FishingEntry>>,
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Day headers
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightText
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar days
        val firstDayOfMonth = (currentMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val days = (1..daysInMonth).map { day ->
            (currentMonth.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            // Empty cells before first day
            items(startDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            
            // Day cells
            items(days) { date ->
                val hasEntries = entriesByDate.containsKey(date.timeInMillis)
                val isSelected = selectedDate?.timeInMillis == date.timeInMillis
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(
                            color = when {
                                isSelected -> IceBlue
                                hasEntries -> GlacierBlue
                                else -> androidx.compose.ui.graphics.Color.Transparent
                            },
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable(enabled = hasEntries) { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${date.get(Calendar.DAY_OF_MONTH)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isSelected -> SnowWhite
                            hasEntries -> DeepWater
                            else -> LightText
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarEntryCard(
    entry: FishingEntry,
    onClick: () -> Unit
) {
    IceLureCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.baitType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkText
                )
                Text(
                    text = "${entry.baitColor.displayName} • ${entry.targetFish.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightText
                )
            }
            ResultBadge(result = entry.result)
        }
    }
}

