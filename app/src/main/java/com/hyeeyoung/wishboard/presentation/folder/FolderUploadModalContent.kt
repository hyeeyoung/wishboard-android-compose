package com.hyeeyoung.wishboard.presentation.folder

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState

private const val MAX_LENGTH_FOLDER_NAME = 10

@Composable
fun FolderUploadModalContent(
    folderName: String? = null,
    uploadState: WishBoardState<Unit>,
    existingFolderName: String?,
    onClickComplete: (String) -> Unit,
) {
    val nameInput = remember { mutableStateOf(folderName ?: "") }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Spacer(modifier = Modifier.height(76.dp))

        WishBoardTextField(
            input = nameInput,
            isError = nameInput.value.isNotBlank() && nameInput.value == existingFolderName,
            errorHidingStrategy = View.INVISIBLE,
            placeholder = stringResource(id = R.string.modal_folder_upload_placeholder),
            errorMsg = stringResource(id = R.string.modal_folder_upload_error),
            maxLength = MAX_LENGTH_FOLDER_NAME,
            bottomEndComponent = {
                Text(
                    text = stringResource(
                        id = R.string.text_length,
                        nameInput.value.length,
                        MAX_LENGTH_FOLDER_NAME,
                    ),
                    color = WishBoardTheme.colors.gray200,
                    style = WishBoardTheme.typography.suitD3,
                )
            },
        )

        Spacer(modifier = Modifier.height(58.dp))

        WishBoardWideButton(
            enabled = nameInput.value.isNotBlank(),
            state = uploadState,
            onClick = { onClickComplete(nameInput.value) },
            text = if (uploadState !is WishBoardState.Loading) {
                stringResource(id = if (folderName == null) R.string.add else R.string.modal_folder_name_edit_btn_text)
            } else {
                ""
            },
        )
    }
}

@Composable
@Preview
fun PreviewNewFolderModalContent() {
    FolderUploadModalContent(
        folderName = "",
        existingFolderName = null,
        uploadState = WishBoardState.Loading,
        onClickComplete = {},
    )
}

@Composable
@Preview
fun PreviewFolderEditModalContent() {
    FolderUploadModalContent(
        folderName = "셀린느",
        existingFolderName = null,
        uploadState = WishBoardState.Idle,
        onClickComplete = {},
    )
}
