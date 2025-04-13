package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import com.hyeeyoung.wishboard.presentation.wish.component.WishItem

@Composable
fun FolderDetailScreen(
    bottomNavController: NavHostController,
    wishNavController: NavHostController,
    folderName: String,
    folderId: Long,
    viewModel: FolderViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.detailUiModel.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    MainScreen.FolderDetail.ScrollToTopEffect(lazyGridState)

    LaunchedEffect(Unit) {
        viewModel.getFolderDetail(folderId)
    }

    FolderDetailScreen(
        wishItems = uiModel,
        folderName = folderName,
        lazyGridState = lazyGridState,
        onClickItem = { id ->
            wishNavController.navigate("${MainScreen.WishItemDetail.route}/$id")
        },
        onClickBack = bottomNavController::safePopBackStack,
    )
}

@Composable
fun FolderDetailScreen(
    wishItems: List<WishItem>,
    folderName: String,
    lazyGridState: LazyGridState,
    onClickItem: (id: Long) -> Unit,
    onClickBack: () -> Unit,
) {
    Scaffold(topBar = {
        WishBoardTopBar(
            topBarModel = WishBoardTopBarModel(
                title = folderName,
                onClickStartIcon = onClickBack,
            ),
        )
    }) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(
                top = paddingValues.calculateTopPadding(),
            )

        if (wishItems.isEmpty()) {
            WishBoardEmptyView(modifier = contentModifier, guideTextRes = R.string.empty_wishlist_guide_text)
        } else {
            LazyVerticalGrid(
                modifier = contentModifier,
                columns = GridCells.Fixed(2),
                state = lazyGridState,
            ) {
                items(wishItems) { item ->
                    WishItem(
                        wishItem = item,
                        onClickItem = { onClickItem(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewFolderDetailScreen() {
    val wishItem =
        WishItem(
            1L,
            "21SS SAGE SHIRT [4COLOR]",
            "https://url.kr/8vwf1e",
            108000,
        )
    val wishList = List(8) { index: Int -> wishItem.copy(id = index.toLong()) }

    FolderDetailScreen(
        wishItems = wishList,
        folderName = "상의",
        lazyGridState = rememberLazyGridState(),
        onClickItem = {},
        onClickBack = {},
    )
}
