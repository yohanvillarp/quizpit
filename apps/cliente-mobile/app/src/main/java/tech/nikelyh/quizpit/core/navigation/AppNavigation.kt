package tech.nikelyh.quizpit.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.nikelyh.quizpit.feature.home.HomeScreen
import tech.nikelyh.quizpit.feature.store.StoreScreen
import tech.nikelyh.quizpit.ui.components.TopLevelDestination
import tech.nikelyh.quizpit.ui.components.QuizpitBottomBar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPagerScreen(navController: NavController) {
    val pagerState = rememberPagerState(
        initialPage = TopLevelDestination.HOME.ordinal,
        pageCount = { TopLevelDestination.values().size }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val currentDestination = TopLevelDestination.values()[pagerState.currentPage]
            QuizpitBottomBar(
                currentDestination = currentDestination,
                onNavigateToDestination = { destination ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(destination.ordinal)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (TopLevelDestination.values()[page]) {
                TopLevelDestination.STORE -> StoreScreen()
                TopLevelDestination.FAMILIARS -> tech.nikelyh.quizpit.feature.familiars.FamiliarsScreen(
                    onBackClick = { coroutineScope.launch { pagerState.animateScrollToPage(TopLevelDestination.HOME.ordinal) } }
                )
                TopLevelDestination.HOME -> HomeScreen(
                    onNavigateToCreateGame = { navController.navigate(Screen.CreateGame.route) },
                    onNavigateToProfile = { coroutineScope.launch { pagerState.animateScrollToPage(TopLevelDestination.PROFILE.ordinal) } }
                )
                TopLevelDestination.ROULETTE -> tech.nikelyh.quizpit.feature.roulette.RouletteScreen()
                TopLevelDestination.PROFILE -> tech.nikelyh.quizpit.feature.profile.ProfileScreen()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            tech.nikelyh.quizpit.feature.splash.SplashScreen(
                onSplashFinished = {
                    navController.navigate("main_pager") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main_pager") {
            MainPagerScreen(navController = navController)
        }
        
        composable(
            route = Screen.CreateGame.route,
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLobby = { roomId ->
                    navController.navigate(NavArgScreen.Lobby.createRoute(roomId)) {
                        popUpTo("main_pager") {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(
            route = NavArgScreen.Lobby.routeWithArgs
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString(NavArgScreen.Lobby.argRoomId) ?: ""
            tech.nikelyh.quizpit.feature.lobby.LobbyScreen(
                roomId = roomId,
                onNavigateBack = {
                    navController.popBackStack("main_pager", false)
                }
            )
        }
    }
}
