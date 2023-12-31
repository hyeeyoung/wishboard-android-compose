package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.dialog.screen.WishBoardDialog
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.presentation.model.Folder
import com.hyeeyoung.wishboard.presentation.util.extension.rememberModalLauncher
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun FolderScreen(navController: NavHostController) {
    val folder = listOf(
        Folder(
            id = 1L,
            name = "아우터",
            thumbnail = "https://url.kr/8vwf1e",
            itemCount = 1,
        ),
    )
    val folders = List(8) { folder }.flatten() // TODO 서버 연동 후 삭제
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    var modalData by remember { mutableStateOf<ModalData?>(null) }
    val context = LocalContext.current

    val modalLauncher = rememberModalLauncher { isTopOption, data ->
        when (data) {
            is ModalData.OptionModal.FolderMore -> {
                if (isTopOption) {
                    modalData = ModalData.Modal.FolderNameEdit(data.folderId, data.folderName)
                } else {
                    dialogData = DialogData.FolderDelete(folderId = data.folderId)
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(modalData) {
        modalData?.openModal(context, modalLauncher)
    }

    WishBoardDialog(
        dialogData = dialogData,
        onClickConfirm = { /*TODO*/ },
        onDismissRequest = { dialogData = null },
    )

    Scaffold(topBar = {
        WishBoardMainTopBar(
            titleRes = R.string.folder,
            endComponent = {
                WishBoardIconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    iconRes = R.drawable.ic_plus,
                    onClick = { ModalData.Modal.NewFolder().openModal(context, modalLauncher) },
                )
            },
        )
    }) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .background(WishBoardTheme.colors.white)
            .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp)

        if (folders.isEmpty()) {
            WishBoardEmptyView(modifier = contentModifier, guideTextRes = R.string.empty_folder_guide_text)
        } else {
            LazyVerticalGrid(
                modifier = contentModifier,
                columns = GridCells.Fixed(2),
            ) {
                items(folders) { folder ->
                    FolderItem(
                        folder = folder,
                        onClickFolder = { folderId ->
                            navController.navigate("${MainScreen.FolderDetail.route}/$folderId/${folder.name}")
                        },
                        onClickMore = { selectedFolder ->
                            modalData = ModalData.OptionModal.FolderMore(selectedFolder.id, selectedFolder.name)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FolderItem(folder: Folder, onClickFolder: (Long) -> Unit, onClickMore: (Folder) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .noRippleClickable { onClickFolder(folder.id) },
    ) {
        ColoredImage(
            model = folder.thumbnail,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp)),
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
                )
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = stringResource(
                        id = R.string.folder_wish_item_count,
                        formatArgs = arrayOf(folder.itemCount),
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
    FolderScreen(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewFolderItem() {
    val folder = Folder(
        id = 1L,
        name = "Bean Ring Gold",
        thumbnail = "https://url.kr/8vwf1e",
        itemCount = 1,
    )

    FolderItem(
        folder = folder,
        onClickFolder = {},
        onClickMore = {},
    )
}
