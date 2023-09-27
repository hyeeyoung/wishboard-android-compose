package com.hyeeyoung.wishboard.designsystem.component.dialog.temp

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardModal( // TODO stable 버전 되면 사용 고려
    isOpen: Boolean,
    @StringRes titleRes: Int,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (!isOpen) return
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        containerColor = WishBoardTheme.colors.white,
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier
                .heightIn(max = 317.dp)
                .fillMaxWidth(),
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 8.dp),
            ) {
                WishBoardIconButton(
                    modifier = Modifier.background(WishBoardTheme.colors.white),
                    iconRes = R.drawable.ic_close,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    },
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(titleRes),
                    style = WishBoardTheme.typography.suitH3,
                    color = WishBoardTheme.colors.gray700,
                )
                content()
            }
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardModal() {
    WishBoardModal(
        isOpen = true,
        titleRes = R.string.wish_item_link_sharing_upload_noti_setting,
        onDismissRequest = {},
        content = { NotiModalContent(onClickComplete = {}) },
    )
}
