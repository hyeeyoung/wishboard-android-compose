package com.hyeeyoung.wishboard.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
@Composable
inline fun <reified T : ViewModel> getSharedViewModel(
    navController: NavController,
    route: String,
): T {
    val localViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val backStackEntry = remember(navController.currentBackStackEntry) {
        try {
            navController.getBackStackEntry(route)
        } catch (e: Exception) {
            localViewModelStoreOwner
        }
    }
    return hiltViewModel(backStackEntry)
}
