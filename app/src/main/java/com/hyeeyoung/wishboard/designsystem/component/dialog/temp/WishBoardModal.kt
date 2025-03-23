package com.hyeeyoung.wishboard.designsystem.component.dialog.temp

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent

// https://medium.com/@zekromvishwa56789/handling-keyboard-overlap-in-modalbottomsheet-with-jetpack-compose-a-practical-approach-e68db28ff66e
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardModal(
    isOpen: Boolean,
    @StringRes titleRes: Int,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    
    if (!isOpen) return
    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = onDismissRequest,
        containerColor = WishBoardTheme.colors.white,
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier
                .heightIn(max = 317.dp)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 8.dp),
            ) {
                WishBoardIconButton(
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
                    text = stringResource(titleRes),
                    style = WishBoardTheme.typography.suitH3,
                    color = WishBoardTheme.colors.gray700,
                )

                content()
                
                Spacer(modifier = Modifier.navigationBarsPadding())
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
        content = { NotiModalContent(notiInfo = NotiInfo(), onClickComplete = {_, _ ->}) },
    )
}
