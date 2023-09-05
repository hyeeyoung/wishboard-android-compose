package com.hyeeyoung.wishboard.config.navigation.screen

sealed class MainScreen(override val route: String) : Screen {
    object Root : MainScreen(route = "mainRoot") // TODO delete
    object Wishlist : MainScreen(route = "wishlist")
    object WishItemDetail : MainScreen(route = "wishItemDetail") {
        const val ARG_WISH_ITEM_ID: String = "wishItemId"
        val routeWithArg = "$route/{$ARG_WISH_ITEM_ID}"
    }
    object Calendar : MainScreen(route = "calendar")
    object Folder : MainScreen(route = "folder")
    object FolderDetail : MainScreen(route = "folderDetail") {
        const val ARG_FOLDER_ID: String = "folderId"
        const val ARG_FOLDER_NAME: String = "folderName"
        val routeWithArg = "$route/{$ARG_FOLDER_ID}/{$ARG_FOLDER_NAME}"
    }

    object Add : MainScreen(route = "add")
    object Noti : MainScreen(route = "noti")
    object My : MainScreen(route = "my")
    object MyProfile : MainScreen(route = "myProfile")
    object MyPasswordChange : MainScreen(route = "myPasswordChange")

    /** NavGraphBuilder.navigation() 사용 시 파라미터 route + "start" 문자열을 합성해서 startDestination 경로를 만듦   */
    fun makeStartRoute() = when (this) {
        Wishlist, Folder, Add, Noti, My -> this.route + "Start"
        else -> throw IllegalStateException("루트 경로가 될 수 없음.")
    }
}
