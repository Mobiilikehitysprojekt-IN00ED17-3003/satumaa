package fi.antero.satumaa.ui.navigation



sealed class RootRoute(val route: String) {
    data object Login : RootRoute("login")
    data object Onboarding : RootRoute("onboarding")
    data object Menu : RootRoute("menu")

    data object Story : RootRoute("story") {
        fun createRoute(id: String?) = if (id != null) "story?storyId=$id" else route
    }

    data object StoryList : RootRoute("story_list")
    data object Letter : RootRoute("letter")
    data object Profile : RootRoute("profile")
}
