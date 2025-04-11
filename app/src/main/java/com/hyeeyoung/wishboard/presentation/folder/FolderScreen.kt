package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardTwoButtonDialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.WishBoardModal
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.component.image.WishBoardPlaceHolder
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.folder.model.FolderTabUiModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(navController: NavHostController, viewModel: FolderViewModel = hiltViewModel()) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    var modalData by remember { mutableStateOf<ModalData.Modal?>(null) }

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    LaunchedEffect(Unit) {
        viewModel.getFolders()
    }

    PullToRefreshBox(
        isRefreshing = uiModel.isRefreshing,
        onRefresh = {
            viewModel.getFolders(true)
        },
    ) {
        FolderScreen(
            uiModel = uiModel,
            onClickFolder = { folder ->
                navController.navigate("${MainScreen.FolderDetail.route}/${folder.id}/${folder.name}")
            },
            deleteFolder = { id ->
                viewModel.deleteFolder(id)
            },
            showModal = { modal: ModalData.Modal ->
                modalData = modal
            },
            clearModalData = {
                viewModel.clearModalData()
            }
        )
    }

    WishBoardModal(
        isOpen = modalData != null,
        titleRes = modalData?.title ?: return,
        onDismissRequest = {
            modalData = null
        },
        content = {
            when (modalData) {
                is ModalData.Modal.NewFolder -> {
                    FolderUploadModalContent(
                        folderName = null,
                        uploadState = uiModel.addState,
                        existingFolderName = uiModel.existingFolderName,
                        onClickComplete = { name ->
                            viewModel.createFolder(folderName = name, afterSuccess = {
                                modalData = null
                            })
                        })
                }

                is ModalData.Modal.FolderNameEdit -> {
                    val data = modalData as ModalData.Modal.FolderNameEdit
                    FolderUploadModalContent(
                        folderName = data.folderName,
                        uploadState = uiModel.updateState,
                        existingFolderName = uiModel.existingFolderName,
                        onClickComplete = { name ->
                            viewModel.updateFolder(folderId = data.folderId, folderName = name, afterSuccess = {
                                modalData = null
                            })
                        },
                    )
                }

                else -> {}
            }
        }
    )
}

@Composable
fun FolderScreen(
    uiModel: FolderTabUiModel,
    onClickFolder: (FolderItem) -> Unit,
    deleteFolder: (id: Long?) -> Unit,
    showModal: (ModalData.Modal) -> Unit,
    clearModalData: () -> Unit,
) {
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val context = LocalContext.current
    val modalLauncher = rememberModalLauncher { isTopOption, data ->
        clearModalData()

        when (data) {
            is ModalData.OptionModal.FolderMore -> {
                if (isTopOption) {
                    showModal(ModalData.Modal.FolderNameEdit(folderId = data.folderId, folderName = data.folderName))

                } else {
                    dialogData = DialogData.FolderDelete(folderId = data.folderId)
                }
            }

            else -> {}
        }
    }

    WishBoardTwoButtonDialog(
        dialogData = dialogData,
        onClickConfirm = {
            when (dialogData) {
                is DialogData.FolderDelete -> {
                    val id = (dialogData as DialogData.FolderDelete).folderId
                    deleteFolder(id)
                }

                else -> {}
            }
        },
        onDismissRequest = { dialogData = null },
    )

    Scaffold(topBar = {
        WishBoardMainTopBar(
            titleRes = R.string.folder,
            endComponent = {
                WishBoardIconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    iconRes = R.drawable.ic_plus,
                    onClick = {
                        clearModalData()
                        showModal(ModalData.Modal.NewFolder(folderName = ""))
                    },
                )
            },
        )
    }) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)

        if (uiModel.folders.isEmpty()) {
            LazyColumn(
                modifier = contentModifier,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    WishBoardEmptyView(modifier = contentModifier, guideTextRes = R.string.empty_folder_guide_text)
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = contentModifier,
                columns = GridCells.Fixed(2),
            ) {
                items(uiModel.folders) { folder ->
                    FolderItem(
                        folder = folder,
                        onClickFolder = {
                            onClickFolder(folder)
                        },
                        onClickMore = { selectedFolder ->
                            ModalData.OptionModal.FolderMore(selectedFolder.id, selectedFolder.name)
                                .openModal(context = context, resultLauncher = modalLauncher)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FolderItem(folder: FolderItem, onClickFolder: () -> Unit, onClickMore: (FolderItem) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .noRippleClickable { onClickFolder() },
    ) {
        Image(
            model = folder.thumbnail,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
            placeHolder = { modifier ->
                WishBoardPlaceHolder(modifier = modifier)
            }
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 16.dp)
                    .weight(1f),
            ) {
                Text(
                    text = folder.name,
                    style = WishBoardTheme.typography.suitB2,
                    color = WishBoardTheme.colors.gray700,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = stringResource(
                        id = R.string.folder_wish_item_count,
                        formatArgs = arrayOf(folder.numOfWishItem),
                    ),
                    style = WishBoardTheme.typography.suitD3,
                    color = WishBoardTheme.colors.gray300,
                )
            }

            Icon(
                modifier = Modifier
                    .noRippleClickable { onClickMore(folder) }
                    .padding(start = 4.dp),
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = null,
                tint = WishBoardTheme.colors.gray200,
            )
        }
    }
}

@Preview
@Composable
fun PreviewFolderScreen() {
    val folder = FolderItem(
        id = 1L,
        name = "아우터",
        thumbnail = "https://url.kr/8vwf1e",
        numOfWishItem = 1,
    )

    val folders = List(8) { index: Int ->
        folder.copy(id = index.toLong())
    }

    FolderScreen(
        uiModel = FolderTabUiModel(folders = folders),
        onClickFolder = {},
        deleteFolder = {},
        showModal = {},
        clearModalData = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFolderItem() {
    val folder = FolderItem(
        id = 1L,
        name = "Bean Ring Gold",
        thumbnail = "https://url.kr/8vwf1e",
        numOfWishItem = 1,
    )

    FolderItem(
        folder = folder,
        onClickFolder = {},
        onClickMore = {},
    )
}
