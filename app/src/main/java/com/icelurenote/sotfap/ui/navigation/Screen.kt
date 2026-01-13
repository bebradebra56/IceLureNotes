package com.icelurenote.sotfap.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Journal : Screen("journal")
    object AddEntry : Screen("add_entry?entryId={entryId}") {
        fun createRoute(entryId: Long? = null): String {
            return if (entryId != null) "add_entry?entryId=$entryId" else "add_entry"
        }
    }
    object EntryDetail : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: Long): String = "entry_detail/$entryId"
    }
    object Baits : Screen("baits")
    object BaitDetail : Screen("bait_detail/{baitType}") {
        fun createRoute(baitType: String): String = "bait_detail/$baitType"
    }
    object Results : Screen("results")
    object Stats : Screen("stats")
    object Calendar : Screen("calendar")
    object Export : Screen("export")
    object Settings : Screen("settings")
}

