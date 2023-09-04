package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.domain.model.WishItem
import com.hyeeyoung.wishboard.presentation.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.wish.component.WishItem

@Composable
fun FolderDetailScreen(navController: NavHostController, folderId: Long, folderName: String) {
    val wishItem = listOf(
        WishItem(
            1L,
            "21SS SAGE SHIRT [4COLOR]",
            "https://url.kr/8vwf1e",
            108000,
            true,
        ),
    )
    val wishList = List(8) { wishItem }.flatten() // TODO 서버 연동 후 삭제

    WishboardTheme {
        Scaffold(topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(
                    title = folderName,
                    onClickStartIcon = { navController.popBackStack() },
                ),
            )
        }) { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                    ),
                columns = GridCells.Fixed(2),
            ) {
                items(wishList) { wishItem ->
                    WishItem(
                        modifier = Modifier.noRippleClickable {
                            navController.navigate("${MainScreen.WishItemDetail.route}/${wishItem.id}")
                        },
                        wishItem = wishItem,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewFolderDetailScreen() {
    FolderDetailScreen(navController = rememberNavController(), folderId = 1L, folderName = "상의")
}
