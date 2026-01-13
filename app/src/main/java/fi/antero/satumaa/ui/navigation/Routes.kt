// app/src/main/java/fi/antero/satumaa/ui/navigation/Routes.kt
package fi.antero.satumaa.ui.navigation

sealed class RootRoute(val route: String) {
    object Login : RootRoute("login")

    object Onboarding : RootRoute("onboarding")
    object Menu : RootRoute("menu")
    object Story : RootRoute("story")
    object Letter : RootRoute("letter")
    object Profile : RootRoute("profile")
}