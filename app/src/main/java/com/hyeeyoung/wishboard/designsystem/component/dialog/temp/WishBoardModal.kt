package com.hyeeyoung.wishboard.designsystem.component.dialog.temp

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.LegacyWishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

// https://medium.com/@zekromvishwa56789/handling-keyboard-overlap-in-modalbottomsheet-with-jetpack-compose-a-practical-approach-e68db28ff66e
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardModal(
    isOpen: Boolean,
    @StringRes titleRes: Int?,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (!isOpen) return
    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = onDismissRequest,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        containerColor = WishBoardTheme.colors.white,
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 8.dp),
            ) {
                LegacyWishBoardIconButton(
                    modifier = Modifier.background(WishBoardTheme.colors.white),
                    iconRes = R.drawable.ic_close,
                    onClick = onDismissRequest,
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    text = if (titleRes != null) stringResource(titleRes) else "",
                    style = WishBoardTheme.typography.suitH3,
                    color = WishBoardTheme.colors.gray700,
                )

                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardModal(
    isOpen: Boolean,
    sheetState: SheetState,
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (!isOpen) return
    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = onDismissRequest,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        containerColor = WishBoardTheme.colors.white,
        properties = properties,
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardModal() {
    WishBoardModal(
        isOpen = true,
        titleRes = R.string.modal_new_folder_title,
        onDismissRequest = {},
        content = {
            FolderUploadModalContent(
                folderName = null,
                uploadState = WishBoardState.Idle,
                existingFolderName = null,
                onClickComplete = { },
            )
        },
    )
}
