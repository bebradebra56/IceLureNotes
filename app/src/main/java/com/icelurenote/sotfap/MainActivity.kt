package com.icelurenote.sotfap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.icelurenote.sotfap.data.database.AppDatabase
import com.icelurenote.sotfap.data.preferences.PreferencesManager
import com.icelurenote.sotfap.ui.navigation.AppNavigation
import com.icelurenote.sotfap.ui.theme.IceLureNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(applicationContext)
        val preferencesManager = PreferencesManager(applicationContext)
        
        setContent {
            IceLureNotesTheme {
                AppNavigation(
                    database = database,
                    preferencesManager = preferencesManager
                )
            }
        }
    }
}