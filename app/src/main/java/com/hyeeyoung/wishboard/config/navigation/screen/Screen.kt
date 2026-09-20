package com.hyeeyoung.wishboard.config.navigation.screen

sealed interface Screen {
    val route: String
}

object Intro : Screen {
    override val route: String
        get() = "intro"
}

object Cart : Screen {
    override val route: String
        get() = "cart"
}

object Calendar : Screen {
    override val route: String
        get() = "calendar"
}

object WishItemDetail : Screen {
    override val route: String
        get() = "wishItemDetail"
}

object WebView : Screen {
    /** TODO URL 추가 */
    override val route: String
        get() = "webView"
    const val ARG_URL = "url"
    const val ARG_TITLE = "title"
    val routeWithArg = "$route?$ARG_URL={$ARG_URL}&$ARG_TITLE={$ARG_TITLE}"
}
