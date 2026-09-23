package tech.nikelyh.quizpit.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import tech.nikelyh.quizpit.R
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import tech.nikelyh.quizpit.core.domain.EnergyManager

enum class TopLevelDestination(
    @androidx.annotation.StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {
    STORE(R.string.nav_store, R.drawable.ic_ink),
    FAMILIARS(R.string.nav_familiars, R.drawable.ic_familiars),
    HOME(R.string.nav_home, R.drawable.ic_home),
    ROULETTE(R.string.nav_roulette, R.drawable.ic_roulette),
    PROFILE(R.string.nav_profile, R.drawable.ic_profile)
}

@Composable
fun QuizpitBottomBar(
    currentDestination: TopLevelDestination,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {
    val inkDrops by EnergyManager.inkDrops.collectAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        TopLevelDestination.values().forEach { destination ->
            val title = androidx.compose.ui.res.stringResource(id = destination.titleRes)
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    if (destination == TopLevelDestination.STORE) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(inkDrops.toString())
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = destination.iconRes),
                                contentDescription = title
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = destination.iconRes),
                            contentDescription = title
                        )
                    }
                },
                label = { Text(title) },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}
