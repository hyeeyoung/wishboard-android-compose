package com.hyeeyoung.wishboard.presentation.folder

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardNarrowButton
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderThumbnailImage
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack

// 선택된 사진(또는 아무것도 선택되지 않았을 때 모든 사진)에 씌우는 10% 검정 오버레이
private val ThumbnailDimColor = Color(0x1A000000)

@Composable
fun FolderThumbnailScreen(
    navController: NavController,
    viewModel: FolderThumbnailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    BackHandler {
        navController.safePopBackStack()
    }

    FolderThumbnailScreen(
        folderName = viewModel.folderName,
        uiState = uiState,
        onClickImage = viewModel::onClickImage,
        onClickSave = {
            viewModel.save { navController.safePopBackStack() }
        },
        onClickBack = { navController.safePopBackStack() },
    )
}

@Composable
fun FolderThumbnailScreen(
    folderName: String,
    uiState: FolderThumbnailUiState,
    onClickImage: (FolderThumbnailImage) -> Unit,
    onClickSave: () -> Unit,
    onClickBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(title = folderName, onClickStartIcon = onClickBack),
                endComponent = { modifier ->
                    WishBoardNarrowButton(
                        modifier = modifier.padding(end = dimensionResource(R.dimen.spacing_base)),
                        enabled = uiState.isSaveEnabled,
                        onClick = onClickSave,
                        text = stringResource(R.string.save),
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(WishBoardTheme.colors.white),
        ) {
            when {
                uiState.fetchState is WishBoardState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThreeDotsLoadingView()
                    }
                }

                uiState.isEmpty -> {
                    WishBoardEmptyView(
                        modifier = Modifier.fillMaxSize(),
                        guideTextRes = R.string.folder_thumbnail_empty_guide,
                    )
                }

                else -> {
                    val hasSelection = uiState.selectedImageId != null

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                    ) {
                        items(uiState.images, key = { it.itemImageId }) { image ->
                            ThumbnailCandidate(
                                image = image,
                                isSelected = image.itemImageId == uiState.selectedImageId,
                                hasSelection = hasSelection,
                                onClick = { onClickImage(image) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 대표 사진 후보 한 칸.
 * - 아무것도 선택되지 않은 상태: 모든 칸에 오버레이와 빈 체크 원을 보여준다.
 * - 하나가 선택된 상태: 선택된 칸에만 오버레이와 체크 원을 보여주고 나머지는 그대로 둔다.
 */
@Composable
private fun ThumbnailCandidate(
    image: FolderThumbnailImage,
    isSelected: Boolean,
    hasSelection: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .noRippleClickable { onClick() },
    ) {
        Image(
            model = image.imageUrl,
            modifier = Modifier.fillMaxSize(),
            placeHolder = { placeHolderModifier ->
                Box(modifier = placeHolderModifier.background(WishBoardTheme.colors.gray100))
            },
        )

        if (isSelected || !hasSelection) {
            Box(modifier = Modifier.fillMaxSize().background(ThumbnailDimColor))

            ThumbnailCheckCircle(
                isSelected = isSelected,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
            )
        }
    }
}

@Composable
private fun ThumbnailCheckCircle(isSelected: Boolean, modifier: Modifier = Modifier) {
    if (isSelected) {
        Box(
            modifier = modifier
                .size(24.dp)
                .background(WishBoardTheme.colors.black, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.ic_check_white),
                contentDescription = "대표 사진으로 선택됨",
                tint = WishBoardTheme.colors.white,
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(24.dp)
                .border(1.5.dp, WishBoardTheme.colors.white, CircleShape),
        )
    }
}
