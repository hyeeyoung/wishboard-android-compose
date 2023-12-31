package com.hyeeyoung.wishboard.designsystem.component.dialog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.DialogData
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme

@Composable
fun WishBoardDialog(
    dialogData: DialogData?,
    onClickConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    if (dialogData == null) return
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = WishBoardTheme.colors.white, shape = RoundedCornerShape(16.dp))
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(dialogData.dialogTextRes.titleRes),
                style = WishBoardTheme.typography.suitH3,
                color = WishBoardTheme.colors.gray600,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
                text = stringResource(dialogData.dialogTextRes.descriptionRes),
                style = WishBoardTheme.typography.suitD2M,
                color = WishBoardTheme.colors.gray300,
                textAlign = TextAlign.Center,
            )

            if (content == null) {
                Spacer(modifier = Modifier.size(32.dp))
            } else {
                content()
            }

            WishBoardDivider()

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = WishBoardTheme.colors.gray150),
                        ) { onDismissRequest() }
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    text = stringResource(id = dialogData.dialogTextRes.dismissBtnTextRes),
                    style = WishBoardTheme.typography.suitB3,
                    color = WishBoardTheme.colors.gray600,
                    textAlign = TextAlign.Center,
                )

                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                    color = WishBoardTheme.colors.gray100,
                )

                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = WishBoardTheme.colors.gray150),
                        ) {
                            onClickConfirm()
                            onDismissRequest()
                        }
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    text = stringResource(id = dialogData.dialogTextRes.confirmBtnTextRes),
                    style = WishBoardTheme.typography.suitB3,
                    color =
                    if (dialogData.isWarningDialog) {
                        WishBoardTheme.colors.pink700
                    } else {
                        WishBoardTheme.colors.green700
                    },
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWishBoardDialog() {
    WishBoardDialog(
        dialogData = DialogData.WishItemDelete(1L),
        onClickConfirm = {},
        onDismissRequest = {},
    )
}
