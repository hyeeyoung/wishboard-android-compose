package com.hyeeyoung.wishboard.presentation.folder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.textfield.WishBoardTextField

@Composable
fun FolderUploadModalContent(folder: Pair<Long, String>? = null, onClickComplete: (String) -> Unit) {
    val nameInput = remember { mutableStateOf(folder?.second ?: "") }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            WishBoardTextField(
                input = nameInput,
                placeholder = stringResource(id = R.string.modal_folder_upload_placeholder),
                errorMsg = stringResource(id = R.string.modal_folder_upload_error),
            )
        }

        WishBoardWideButton(
            enabled = true,
            onClick = { onClickComplete(nameInput.value) },
            text = stringResource(id = if (folder == null) R.string.add else R.string.modal_folder_name_edit_btn_text),
        )
    }
}

@Composable
@Preview
fun PreviewNewFolderModalContent() {
    FolderUploadModalContent(onClickComplete = {})
}

@Composable
@Preview
fun PreviewFolderEditModalContent() {
    FolderUploadModalContent(Pair(1L, "셀린느"), onClickComplete = {})
}
