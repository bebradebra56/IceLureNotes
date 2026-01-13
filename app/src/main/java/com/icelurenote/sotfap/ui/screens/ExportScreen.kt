package com.icelurenote.sotfap.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.ui.components.PrimaryButton
import com.icelurenote.sotfap.ui.theme.BackgroundLight
import com.icelurenote.sotfap.ui.theme.DarkText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    entries: List<FishingEntry>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Data") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Export to CSV",
                style = MaterialTheme.typography.titleLarge,
                color = DarkText
            )
            
            Text(
                text = "Export all your fishing entries to a CSV file. You can open this file in spreadsheet applications like Excel or Google Sheets.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow("Total Entries", "${entries.size}")
                    InfoRow("Date Range", getDateRange(entries))
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = if (isExporting) "Exporting..." else "Export to CSV",
                onClick = {
                    scope.launch {
                        isExporting = true
                        errorMessage = null
                        try {
                            exportToCSV(context, entries)
                            showSuccessDialog = true
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Export failed"
                        } finally {
                            isExporting = false
                        }
                    }
                },
                enabled = !isExporting && entries.isNotEmpty()
            )
            
            if (entries.isEmpty()) {
                Text(
                    text = "No entries to export",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            errorMessage?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Export Successful") },
            text = { Text("Your fishing journal has been exported successfully.") },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    onBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun getDateRange(entries: List<FishingEntry>): String {
    if (entries.isEmpty()) return "No entries"
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    val oldestDate = entries.minByOrNull { it.timestamp }?.timestamp
    val newestDate = entries.maxByOrNull { it.timestamp }?.timestamp
    
    return if (oldestDate != null && newestDate != null) {
        "${dateFormat.format(Date(oldestDate))} - ${dateFormat.format(Date(newestDate))}"
    } else {
        "Unknown"
    }
}

private suspend fun exportToCSV(context: Context, entries: List<FishingEntry>) {
    withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val fileName = "ice_lure_notes_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        file.bufferedWriter().use { writer ->
            // Write header
            writer.write("Date,Bait Type,Bait Color,Target Fish,Depth (m),Result,Notes\n")
            
            // Write entries
            entries.sortedByDescending { it.timestamp }.forEach { entry ->
                writer.write(
                    listOf(
                        dateFormat.format(Date(entry.timestamp)),
                        entry.baitType.displayName,
                        entry.baitColor.displayName,
                        entry.targetFish.displayName,
                        entry.depth.toString(),
                        entry.result.displayName,
                        "\"${entry.notes.replace("\"", "\"\"")}\""
                    ).joinToString(",") + "\n"
                )
            }
        }
        
        // Share the file
        withContext(Dispatchers.Main) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Export CSV"))
        }
    }
}

