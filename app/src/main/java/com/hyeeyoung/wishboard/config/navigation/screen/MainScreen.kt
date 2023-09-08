package com.hyeeyoung.wishboard.config.navigation.screen

sealed class MainScreen(override val route: String) : Screen {
    object Root : MainScreen(route = "mainRoot") // TODO delete
    object Wishlist : MainScreen(route = "wishlist")
    object WishItemDetail : MainScreen(route = "wishItemDetail") {
        const val ARG_WISH_ITEM_ID: String = "wishItemId"
        val routeWithArg = "$route/{$ARG_WISH_ITEM_ID}"
    }

    object Calendar : MainScreen(route = "calendar")
    object Cart : MainScreen(route = "cart")
    object Folder : MainScreen(route = "folder")
    object FolderDetail : MainScreen(route = "folderDetail") {
        const val ARG_FOLDER_ID: String = "folderId"
        const val ARG_FOLDER_NAME: String = "folderName"
        val routeWithArg = "$route/{$ARG_FOLDER_ID}/{$ARG_FOLDER_NAME}"
    }

    object Upload : MainScreen(route = "upload") {
        const val ARG_ITEM_DETAIL: String = "itemDetail"
        val routeWithArg = "$route?$ARG_ITEM_DETAIL={$ARG_ITEM_DETAIL}"
    }

    object Noti : MainScreen(route = "noti")
    object My : MainScreen(route = "my")
    object MyProfile : MainScreen(route = "myProfile")
    object MyPasswordChange : MainScreen(route = "myPasswordChange")

    /** 메인 바텀바 메뉴에서 해당 메뉴의 시작 루트를 반환. 단, NavGraphBuilder.navigation() 사용 시 startDestination 경로는 파라미터 route + "start" 합성 */
    fun getStartRouteForMainTab(): String = when (this) {
        Wishlist, Upload, Noti -> this.route
        Folder, My -> this.route + "Start"
        else -> throw IllegalStateException("StartDestination이 정의되어있지 않음.")
    }
}
