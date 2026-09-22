package tech.nikelyh.quizpit.core.navigation

/**
 * Rutas de la aplicación para navegación segura.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Store : Screen("store")
    object Inventory : Screen("inventory")
    object Roulette : Screen("roulette")
    object Profile : Screen("profile")
    object CreateGame : Screen("create_game")

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Home.route -> Home
                Store.route -> Store
                Inventory.route -> Inventory
                Roulette.route -> Roulette
                Profile.route -> Profile
                CreateGame.route -> CreateGame
                else -> Home
            }
        }
    }
}

/**
 * Rutas con parámetros.
 */
sealed class NavArgScreen(val baseRoute: String) {
    object Lobby : NavArgScreen("lobby") {
        fun createRoute(roomId: String) = "$baseRoute/$roomId"
        const val routeWithArgs = "lobby/{roomId}"
        const val argRoomId = "roomId"
    }
}
