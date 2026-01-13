package com.icelurenote.sotfap.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.icelurenote.sotfap.data.model.UnitPreference
import com.icelurenote.sotfap.ui.components.IceLureCard
import com.icelurenote.sotfap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    unitPreference: UnitPreference,
    onUnitPreferenceChange: (UnitPreference) -> Unit,
    onResetData: () -> Unit,
    onExportClick: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            // Data Section
            SectionHeader("Data")
            
            SettingItem(
                icon = "📤",
                title = "Export Journal",
                subtitle = "Export your entries to CSV",
                onClick = onExportClick
            )
            
            // Preferences Section
            SectionHeader("Preferences")
            
            SettingItem(
                icon = "📏",
                title = "Depth Unit",
                subtitle = unitPreference.displayName,
                onClick = { showUnitDialog = true }
            )
            
            // Danger Zone
            SectionHeader("Danger Zone")
            
            SettingItem(
                icon = "🗑️",
                title = "Reset All Data",
                subtitle = "Delete all entries permanently",
                onClick = { showResetDialog = true },
                isDangerous = true
            )

            SectionHeader("About")

            SettingItem(
                icon = "\uD83D\uDCCB️",
                title = "Privacy Policy",
                subtitle = "Tap to read",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://icelurenotes.com/privacy-policy.html"))
                    context.startActivity(intent)
                },
            )
            
            Spacer(modifier = Modifier.weight(1f))

        }
    }
    
    // Unit Selection Dialog
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = { Text("Depth Unit") },
            text = {
                Column {
                    UnitPreference.entries.forEach { unit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = unitPreference == unit,
                                onClick = {
                                    onUnitPreferenceChange(unit)
                                    showUnitDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(unit.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data") },
            text = { 
                Text("Are you sure you want to delete all entries? This action cannot be undone and all your fishing data will be permanently lost.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetData()
                    }
                ) {
                    Text("Delete All", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        style = MaterialTheme.typography.titleSmall,
        color = LightText
    )
}

@Composable
private fun SettingItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDangerous: Boolean = false
) {
    IceLureCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isDangerous) DangerRed else DarkText
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDangerous) DangerRed.copy(alpha = 0.7f) else LightText
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isDangerous) DangerRed else IceShadow
            )
        }
    }
}

