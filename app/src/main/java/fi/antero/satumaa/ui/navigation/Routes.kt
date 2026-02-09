package fi.antero.satumaa.ui.navigation

/**
 * Määrittelee sovelluksen päätason reitit (Routes).
 * Sealed class varmistaa tyyppiturvallisuuden.
 */
sealed class RootRoute(val route: String) {
    data object Login : RootRoute("login")
    data object Onboarding : RootRoute("onboarding")
    data object Menu : RootRoute("menu")

    // Satu-näkymä (parametrina valinnainen storyId)
    data object Story : RootRoute("story") {
        fun createRoute(id: String?) = if (id != null) "$route?storyId=$id" else route
    }

    data object StoryList : RootRoute("story_list")

    // Kirje-näkymä (parametrina valinnainen letterId)
    data object Letter : RootRoute("letter") {
        fun createRoute(id: String?) = if (id != null) "$route?letterId=$id" else route
    }

    data object LetterList : RootRoute("letter_list")

    // Karttanäkymä kirjeelle
    data object LetterMap : RootRoute("letter_map")

    data object Profile : RootRoute("profile")
}