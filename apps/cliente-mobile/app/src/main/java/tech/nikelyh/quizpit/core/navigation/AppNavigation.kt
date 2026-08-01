package tech.nikelyh.quizpit.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tech.nikelyh.quizpit.feature.home.HomeScreen
import tech.nikelyh.quizpit.feature.inventory.InventoryScreen
import tech.nikelyh.quizpit.feature.store.StoreScreen
import tech.nikelyh.quizpit.ui.components.QuizpitBottomBar
import tech.nikelyh.quizpit.ui.components.TopLevelDestination

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Mapeamos la ruta actual al destino enum correspondiente
    val currentDestination = when (currentRoute) {
        "inventory" -> TopLevelDestination.FAMILIARS
        "store" -> TopLevelDestination.STORE
        else -> TopLevelDestination.HOME
    }

    Scaffold(
        bottomBar = {
            QuizpitBottomBar(
                currentDestination = currentDestination,
                onNavigateToDestination = { destination ->
                    val route = when (destination) {
                        TopLevelDestination.FAMILIARS -> "inventory"
                        TopLevelDestination.HOME -> "home"
                        TopLevelDestination.STORE -> "store"
                    }
                    // Solo navegar si la ruta es diferente a la actual
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("inventory") {
                InventoryScreen()
            }
            composable("home") {
                HomeScreen()
            }
            composable("store") {
                StoreScreen()
            }
        }
    }
}
