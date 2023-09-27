package com.hyeeyoung.wishboard.designsystem.component.dialog.temp

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishBoardTwoOptionModal( // TODO stable 버전 되면 사용 고려
    isOpen: Boolean,
    @StringRes topOption: Int,
    @StringRes bottomOption: Int,
    onClickTop: () -> Unit = {},
    onClickBottom: () -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (!isOpen) return
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier.background(
                    color = WishBoardTheme.colors.white,
                    shape = RoundedCornerShape(16.dp),
                ),
            ) {
                Text(
                    modifier = Modifier
                        .noRippleClickable {
                            onClickTop()
                            onDismissRequest()
                        }
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(topOption),
                    style = WishBoardTheme.typography.suitB3,
                    color = WishBoardTheme.colors.gray600,
                    textAlign = TextAlign.Center,
                )
                WishBoardDivider()
                Text(
                    modifier = Modifier
                        .noRippleClickable {
                            onClickBottom()
                            onDismissRequest()
                        }
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(bottomOption),
                    style = WishBoardTheme.typography.suitB3,
                    color = WishBoardTheme.colors.pink700,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismissRequest()
                                }
                            }
                    }
                    .background(color = WishBoardTheme.colors.white, shape = RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp),
                text = stringResource(R.string.cancel),
                style = WishBoardTheme.typography.suitB3,
                color = WishBoardTheme.colors.gray600,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardTwoOptionModal() {
    WishBoardTwoOptionModal(
        isOpen = true,
        topOption = R.string.modal_folder_name_edit_title,
        bottomOption = R.string.dialog_folder_delete_title,
        onDismissRequest = {},
    )
}
