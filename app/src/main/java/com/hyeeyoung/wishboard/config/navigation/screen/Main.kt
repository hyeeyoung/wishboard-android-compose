package com.hyeeyoung.wishboard.config.navigation.screen

import com.hyeeyoung.wishboard.config.navigation.screen.Main.FolderDetail.ARG_FOLDER_ID
import com.hyeeyoung.wishboard.config.navigation.screen.Main.FolderDetail.ARG_FOLDER_NAME

sealed class Main(override val route: String) : Screen {
    object Root : Main(route = "mainRoot")
    object Wishlist : Main(route = "wishlist")
    object WishItemDetail : Main(route = "wishItemDetail") {
        const val ARG_WISH_ITEM_ID: String = "wishItemId"
        val routeWithArg = "$route/{$ARG_WISH_ITEM_ID}"
    }
    object Folder : Main(route = "folder")
    object FolderDetail : Main(route = "folderDetail") {
        const val ARG_FOLDER_ID: String = "folderId"
        const val ARG_FOLDER_NAME: String = "folderName"
        val routeWithArg = "$route/{$ARG_FOLDER_ID}/{$ARG_FOLDER_NAME}"
    }
    object Add : Main(route = "add")
    object Noti : Main(route = "noti")
    object My : Main(route = "my")

    /** NavGraphBuilder.navigation() 사용 시 파라미터 route + "start" 문자열을 합성해서 startDestination 경로를 만듦   */
    fun makeStartRoute() = when (this) {
        Wishlist, Folder, Add, Noti, My -> this.route + "Start"
        else -> throw IllegalStateException("루트 경로가 될 수 없음.")
    }
}
