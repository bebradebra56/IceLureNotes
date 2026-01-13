package com.icelurenote.sotfap.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.icelurenote.sotfap.data.database.AppDatabase
import com.icelurenote.sotfap.data.model.BaitType
import com.icelurenote.sotfap.data.model.UnitPreference
import com.icelurenote.sotfap.data.preferences.PreferencesManager
import com.icelurenote.sotfap.data.repository.FishingRepository
import com.icelurenote.sotfap.ui.screens.*
import com.icelurenote.sotfap.ui.theme.BackgroundLight
import com.icelurenote.sotfap.ui.theme.IceBlue
import com.icelurenote.sotfap.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    database: AppDatabase,
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    val repository = remember { FishingRepository(database.fishingEntryDao()) }
    val viewModelFactory = remember { ViewModelFactory(repository, preferencesManager) }
    
    // Wait for onboarding state to load before starting navigation
    val isOnboardingCompleted by preferencesManager.isOnboardingCompleted.collectAsState(initial = null)
    
    // Show loading until we know onboarding status
    if (isOnboardingCompleted == null) {
        // Show minimal splash while loading preference
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = IceBlue)
        }
        return
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { completed ->
                    navController.navigate(
                        if (completed) Screen.Journal.route else Screen.Onboarding.route
                    ) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isOnboardingCompleted = isOnboardingCompleted ?: false
            )
        }
        
        // Onboarding
        composable(Screen.Onboarding.route) {
            val scope = rememberCoroutineScope()
            OnboardingScreen(
                onComplete = {
                    scope.launch {
                        preferencesManager.setOnboardingCompleted(true)
                        navController.navigate(Screen.Journal.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        // Main screens with bottom navigation
        composable(Screen.Journal.route) {
            MainScreen(
                navController = navController,
                viewModelFactory = viewModelFactory,
                repository = repository,
                preferencesManager = preferencesManager
            )
        }
        
        composable(Screen.Baits.route) {
            MainScreen(
                navController = navController,
                viewModelFactory = viewModelFactory,
                repository = repository,
                preferencesManager = preferencesManager
            )
        }
        
        composable(Screen.Results.route) {
            MainScreen(
                navController = navController,
                viewModelFactory = viewModelFactory,
                repository = repository,
                preferencesManager = preferencesManager
            )
        }
        
        composable(Screen.Settings.route) {
            MainScreen(
                navController = navController,
                viewModelFactory = viewModelFactory,
                repository = repository,
                preferencesManager = preferencesManager
            )
        }
        
        // Add Entry
        composable(
            route = Screen.AddEntry.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val viewModel: AddEntryViewModel = viewModel(factory = viewModelFactory)
            val state by viewModel.state.collectAsState()
            val unitPreference by preferencesManager.unitPreference.collectAsState(initial = UnitPreference.METERS)
            val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull()
            
            LaunchedEffect(entryId, unitPreference) {
                if (entryId != null) {
                    viewModel.loadEntry(entryId, unitPreference)
                }
            }
            
            AddEntryScreen(
                state = state,
                unitPreference = unitPreference,
                onBaitTypeChange = viewModel::setBaitType,
                onBaitColorChange = viewModel::setBaitColor,
                onTargetFishChange = viewModel::setTargetFish,
                onDepthChange = viewModel::setDepth,
                onResultChange = viewModel::setResult,
                onNotesChange = viewModel::setNotes,
                onSave = {
                    viewModel.saveEntry(
                        unitPreference = unitPreference,
                        onSuccess = { navController.navigateUp() },
                        onError = { /* Show error */ }
                    )
                },
                onBack = { navController.navigateUp() }
            )
        }
        
        // Entry Detail
        composable(
            route = Screen.EntryDetail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
            val scope = rememberCoroutineScope()
            val unitPreference by preferencesManager.unitPreference.collectAsState(initial = UnitPreference.METERS)
            var entry by remember { mutableStateOf<com.icelurenote.sotfap.data.model.FishingEntry?>(null) }
            
            LaunchedEffect(entryId) {
                entry = repository.getEntryById(entryId)
            }
            
            EntryDetailScreen(
                entry = entry,
                unitPreference = unitPreference,
                onEdit = {
                    navController.navigate(Screen.AddEntry.createRoute(entryId))
                },
                onDelete = {
                    scope.launch {
                        entry?.let { repository.deleteEntry(it) }
                        navController.navigateUp()
                    }
                },
                onBack = { navController.navigateUp() }
            )
        }
        
        // Bait Detail
        composable(
            route = Screen.BaitDetail.route,
            arguments = listOf(navArgument("baitType") { type = NavType.StringType })
        ) { backStackEntry ->
            val baitTypeName = backStackEntry.arguments?.getString("baitType") ?: return@composable
            val baitType = BaitType.valueOf(baitTypeName)
            val allEntries by repository.getAllEntries().collectAsState(initial = emptyList())
            val baitEntries = allEntries.filter { it.baitType == baitType }
            
            BaitDetailScreen(
                baitType = baitType,
                entries = baitEntries,
                onBack = { navController.navigateUp() },
                onEntryClick = { entryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(entryId))
                }
            )
        }
        
        // Stats
        composable(Screen.Stats.route) {
            val baitsViewModel: BaitsViewModel = viewModel(factory = viewModelFactory)
            val baitStatistics by baitsViewModel.baitStatistics.collectAsState()
            val totalEntries by repository.getTotalEntriesCount().collectAsState(initial = 0)
            
            StatsScreen(
                totalEntries = totalEntries,
                baitStatistics = baitStatistics,
                onBack = { navController.navigateUp() }
            )
        }
        
        // Calendar
        composable(Screen.Calendar.route) {
            val allEntries by repository.getAllEntries().collectAsState(initial = emptyList())
            
            CalendarScreen(
                entries = allEntries,
                onBack = { navController.navigateUp() },
                onEntryClick = { entryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(entryId))
                }
            )
        }
        
        // Export
        composable(Screen.Export.route) {
            val allEntries by repository.getAllEntries().collectAsState(initial = emptyList())
            
            ExportScreen(
                entries = allEntries,
                onBack = { navController.navigateUp() }
            )
        }
    }
}

@Composable
private fun MainScreen(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    repository: FishingRepository,
    preferencesManager: PreferencesManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundLight
            ) {
                bottomNavigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Journal.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentRoute) {
                Screen.Journal.route -> {
                    val viewModel: JournalViewModel = viewModel(factory = viewModelFactory)
                    val entries by viewModel.filteredEntries.collectAsState()
                    val selectedFishFilter by viewModel.selectedFishFilter.collectAsState()
                    val selectedResultFilter by viewModel.selectedResultFilter.collectAsState()
                    
                    JournalScreen(
                        entries = entries,
                        selectedFishFilter = selectedFishFilter,
                        selectedResultFilter = selectedResultFilter,
                        onFishFilterChange = viewModel::setFishFilter,
                        onResultFilterChange = viewModel::setResultFilter,
                        onEntryClick = { entryId ->
                            navController.navigate(Screen.EntryDetail.createRoute(entryId))
                        },
                        onAddClick = {
                            navController.navigate(Screen.AddEntry.createRoute())
                        }
                    )
                }
                
                Screen.Baits.route -> {
                    val viewModel: BaitsViewModel = viewModel(factory = viewModelFactory)
                    val baitStatistics by viewModel.baitStatistics.collectAsState()
                    
                    BaitsScreen(
                        baitStatistics = baitStatistics,
                        onBaitClick = { baitTypeName ->
                            navController.navigate(Screen.BaitDetail.createRoute(baitTypeName))
                        }
                    )
                }
                
                Screen.Results.route -> {
                    val viewModel: ResultsViewModel = viewModel(factory = viewModelFactory)
                    val summary by viewModel.resultsSummary.collectAsState()
                    
                    ResultsScreen(
                        summary = summary,
                        onStatsClick = {
                            navController.navigate(Screen.Stats.route)
                        },
                        onCalendarClick = {
                            navController.navigate(Screen.Calendar.route)
                        }
                    )
                }
                
                Screen.Settings.route -> {
                    val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                    val unitPreference by viewModel.unitPreference.collectAsState()
                    
                    SettingsScreen(
                        unitPreference = unitPreference,
                        onUnitPreferenceChange = viewModel::setUnitPreference,
                        onResetData = {
                            viewModel.resetAllData { /* Data reset */ }
                        },
                        onExportClick = {
                            navController.navigate(Screen.Export.route)
                        }
                    )
                }
            }
        }
    }
}

