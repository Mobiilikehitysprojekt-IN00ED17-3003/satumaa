package fi.antero.satumaa.ui.navigation

sealed class RootRoute(val route: String) {
    data object Login : RootRoute("login")
    data object Onboarding : RootRoute("onboarding")
    data object Menu : RootRoute("menu")


    data object Story : RootRoute("story")

    data object Letter : RootRoute("letter")
    data object Profile : RootRoute("profile")
}