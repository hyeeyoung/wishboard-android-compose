package com.hyeeyoung.wishboard.presentation.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable

@Composable
fun WishBoardTwoOptionModal(
    @StringRes topOption: Int,
    @StringRes bottomOption: Int,
    isWarningBottom: Boolean = true,
    onClickTop: () -> Unit = {},
    onClickBottom: () -> Unit = {},
    onDismissRequest: () -> Unit,
) {
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
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
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
                        }
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(bottomOption),
                    style = WishBoardTheme.typography.suitB3,
                    color = if (isWarningBottom) WishBoardTheme.colors.pink700 else WishBoardTheme.colors.gray600,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        onDismissRequest()
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
        topOption = R.string.modal_folder_name_edit_title,
        bottomOption = R.string.dialog_folder_delete_title,
        onDismissRequest = {},
    )
}
