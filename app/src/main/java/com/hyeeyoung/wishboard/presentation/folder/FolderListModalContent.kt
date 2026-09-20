package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun FolderListModalContent(
    selectedFolder: FolderItem?,
    folders: List<FolderItem>,
    onClickFolder: (FolderItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selectedId by remember { mutableStateOf(selectedFolder?.id) }
    Column(modifier = Modifier.heightIn(max = 317.dp)) {
        ModalTitle(
            title = stringResource(id = R.string.folder),
            onDismissRequest = onDismissRequest,
        )

        if (folders.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))

            WishBoardEmptyView(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(WishBoardTheme.colors.white),
                guideTextRes = R.string.empty_folder_guide_text,
            )

            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .background(WishBoardTheme.colors.white),
                contentPadding = PaddingValues(bottom = 64.dp),
            ) {
                itemsIndexed(folders) { idx, folder ->
                    HorizontalFolderItem(
                        folder = folder,
                        isSelected = selectedId == folder.id,
                        onClickFolder = { selectedFolder ->
                            selectedId = selectedFolder.id
                            onClickFolder(selectedFolder)
                        },
                    )
                    if (idx != folders.lastIndex) {
                        WishBoardDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalFolderItem(folder: FolderItem, isSelected: Boolean, onClickFolder: (FolderItem) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .noRippleClickable { onClickFolder(folder) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            model = folder.thumbnail,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )

        Text(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
            text = folder.name,
            style = WishBoardTheme.typography.suitD2,
            color = WishBoardTheme.colors.gray700,
        )

        if (isSelected) {
            Spacer(modifier = Modifier.size(10.dp))
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_check_white),
                contentDescription = null,
                tint = WishBoardTheme.colors.green500,
            )
        }
    }
}

@Composable
@Preview
fun PreviewFolderListModalContent() {
    val folder = FolderItem(
        id = 0L,
        name = "아우터",
        thumbnail = "https://url.kr/8vwf1e",
    )
    val folders = List(8) { index: Int -> folder.copy(id = index.toLong()) }
    FolderListModalContent(folder, folders = folders, onClickFolder = {}, onDismissRequest = {})
}
