package com.hyeeyoung.wishboard.designsystem.component.dialog.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishBoardModal(@StringRes titleRes: Int, onDismissRequest: () -> Unit = {}, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .noRippleClickable { onDismissRequest() },
        )
        Box(
            modifier = Modifier
                .heightIn(max = 317.dp)
                .fillMaxWidth()
                .background(
                    color = WishBoardTheme.colors.white,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 8.dp),
            ) {
                WishBoardIconButton(
                    modifier = Modifier.background(WishBoardTheme.colors.white),
                    iconRes = R.drawable.ic_close,
                    onClick = { onDismissRequest() },
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
fun PreviewFolderUploadModal() {
    WishBoardModal(titleRes = R.string.modal_new_folder_title) {
        FolderUploadModalContent(onClickComplete = {})
    }
}
