package fi.antero.satumaa.ui.navigation

sealed class RootRoute(val route: String) {
    object Login : RootRoute("login")
    object Menu : RootRoute("menu")
    object Story : RootRoute("story")
    object Letter : RootRoute("letter")
    object Profile : RootRoute("profile")
}
