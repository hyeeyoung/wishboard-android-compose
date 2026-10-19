package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.WishBoardEmptyView
import com.hyeeyoung.wishboard.designsystem.component.dialog.temp.ModalTitle
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.image.Image
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

/**
 * 리스트가 소비하고 남은 스크롤/플링을 부모(바텀시트)로 전달하지 않아,
 * 리스트 스크롤이 시트를 내리는 드래그 제스처로 인식되지 않도록 한다.
 */
private val consumeRemainingScrollConnection = object : NestedScrollConnection {
    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset = available

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = available
}

@Composable
fun FolderListModalContent(
    selectedFolder: FolderItem?,
    folders: List<FolderItem>,
    onClickFolder: (FolderItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selectedId by remember { mutableStateOf(selectedFolder?.id) }
    // 폴더 리스트 바텀시트의 전체 높이가 디바이스(화면) 높이의 2/3가 되도록, 시트가 따로 차지하는 내비게이션 바 높이는 제외한다.
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalView.current.rootView.height.toDp() }
        .takeIf { it > 0.dp } ?: LocalConfiguration.current.screenHeightDp.dp
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sheetHeight = windowHeight * 2 / 3 - navigationBarHeight
    Column(modifier = Modifier.height(sheetHeight)) {
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
                    // ModalTitle 높이(45dp)와 합쳐 Figma의 리스트 시작 위치(시트 상단에서 50dp)에 맞춘다.
                    .padding(top = 5.dp)
                    .background(WishBoardTheme.colors.white)
                    .nestedScroll(consumeRemainingScrollConnection),
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
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.spacing_base), vertical = 8.dp)
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

        Spacer(modifier = Modifier.size(10.dp))
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(
                id = if (isSelected) R.drawable.ic_folder_check_on else R.drawable.ic_folder_check_off,
            ),
            contentDescription = null,
            tint = Color.Unspecified,
        )
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
