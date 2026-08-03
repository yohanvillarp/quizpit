package tech.nikelyh.quizpit.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private fun String?.toTopLevelDestination(): TopLevelDestination {
    return when (this) {
        "store" -> TopLevelDestination.STORE
        "inventory" -> TopLevelDestination.FAMILIARS
        "roulette" -> TopLevelDestination.ROULETTE
        "profile" -> TopLevelDestination.PROFILE
        else -> TopLevelDestination.HOME
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Mapeamos la ruta actual al destino enum correspondiente
    val currentDestination = currentRoute.toTopLevelDestination()

    Scaffold(
        bottomBar = {
            androidx.compose.animation.AnimatedVisibility(
                visible = currentRoute != "create_game",
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
                exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it })
            ) {
                QuizpitBottomBar(
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        val route = when (destination) {
                            TopLevelDestination.STORE -> "store"
                            TopLevelDestination.FAMILIARS -> "inventory"
                            TopLevelDestination.HOME -> "home"
                            TopLevelDestination.ROULETTE -> "roulette"
                            TopLevelDestination.PROFILE -> "profile"
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
        }
    ) { innerPadding ->
        // Si estamos en la ruta create_game, ignoramos el padding inferior para que llene la pantalla inmediatamente
        val bottomPadding = if (currentRoute == "create_game") 0.dp else innerPadding.calculateBottomPadding()
        val currentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = bottomPadding
        )

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(currentPadding),
            enterTransition = {
                if (targetState.destination.route == "create_game" || initialState.destination.route == "create_game") {
                    androidx.compose.animation.EnterTransition.None // Se maneja en la ruta específica
                } else {
                    val initialIndex = initialState.destination.route.toTopLevelDestination().ordinal
                    val targetIndex = targetState.destination.route.toTopLevelDestination().ordinal
                    val isMovingRight = targetIndex > initialIndex
                    
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> if (isMovingRight) fullWidth else -fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            },
            exitTransition = {
                if (targetState.destination.route == "create_game" || initialState.destination.route == "create_game") {
                    androidx.compose.animation.ExitTransition.None // Se maneja en la ruta específica
                } else {
                    val initialIndex = initialState.destination.route.toTopLevelDestination().ordinal
                    val targetIndex = targetState.destination.route.toTopLevelDestination().ordinal
                    val isMovingRight = targetIndex > initialIndex
                    
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> if (isMovingRight) -fullWidth else fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            },
            popEnterTransition = {
                if (targetState.destination.route == "create_game" || initialState.destination.route == "create_game") {
                    androidx.compose.animation.EnterTransition.None // Se maneja en la ruta específica
                } else {
                    val initialIndex = initialState.destination.route.toTopLevelDestination().ordinal
                    val targetIndex = targetState.destination.route.toTopLevelDestination().ordinal
                    val isMovingRight = targetIndex > initialIndex
                    
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> if (isMovingRight) fullWidth else -fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            },
            popExitTransition = {
                if (targetState.destination.route == "create_game" || initialState.destination.route == "create_game") {
                    androidx.compose.animation.ExitTransition.None // Se maneja en la ruta específica
                } else {
                    val initialIndex = initialState.destination.route.toTopLevelDestination().ordinal
                    val targetIndex = targetState.destination.route.toTopLevelDestination().ordinal
                    val isMovingRight = targetIndex > initialIndex
                    
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> if (isMovingRight) -fullWidth else fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
        ) {
            composable("store") {
                StoreScreen()
            }
            composable("inventory") {
                InventoryScreen()
            }
            composable("home") {
                HomeScreen(
                    onNavigateToCreateGame = { navController.navigate("create_game") }
                )
            }
            composable(
                route = "create_game",
                enterTransition = {
                    androidx.compose.animation.slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                },
                exitTransition = {
                    androidx.compose.animation.slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                },
                popExitTransition = {
                    androidx.compose.animation.slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            ) {
                tech.nikelyh.quizpit.feature.home.CreateGameScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("roulette") {
                tech.nikelyh.quizpit.feature.roulette.RouletteScreen()
            }
            composable("profile") {
                tech.nikelyh.quizpit.feature.profile.ProfileScreen()
            }
        }
    }
}
