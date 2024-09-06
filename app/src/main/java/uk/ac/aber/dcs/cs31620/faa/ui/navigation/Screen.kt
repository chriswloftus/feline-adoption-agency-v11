package uk.ac.aber.dcs.cs31620.faa.ui.navigation
/**
 * Wraps as objects, singletons for each screen used in
 * navigation. Each has a unique route.
 * @param route To pass through the route string
 * @author Chris Loftus
 */
sealed class Screen(
    val route: String
) {
    data object Home : Screen("home")
    data object Cats : Screen("cats")
    data object Login : Screen("login")
    data object AddCat : Screen("addCat")
    data object Map : Screen("map")
}

val screens = listOf(
    Screen.Home,
    Screen.Cats,
    Screen.Map
)
