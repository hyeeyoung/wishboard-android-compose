package com.hyeeyoung.wishboard.presentation.dialog

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.StringRes
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import kotlinx.datetime.LocalDateTime
import java.io.Serializable

sealed class ModalData : Serializable {
    sealed class Modal(@StringRes val title: Int) : ModalData() {
        data class Noti(
            val notiType: NotiType? = null,
            val notiDate: LocalDateTime? = null,
        ) : Modal(title = R.string.modal_noti_setting_title)

        data class FolderList(val selectedFolderId: Long?, val selectedFolderName: String? = null) : Modal(
            title = R.string.modal_folder_selection_title,
        )

        data class NewFolder(val folderName: String = "") : Modal(title = R.string.modal_new_folder_title) /*TODO*/

        data class FolderNameEdit(
            val folderId: Long,
            val folderName: String,
        ) : Modal(title = R.string.modal_folder_name_edit_title)

        data class ShopLink(
            val link: String,
        ) : Modal(title = R.string.modal_shop_link_title)
    }

    sealed class OptionModal(
        @StringRes val topOption: Int,
        @StringRes val bottomOption: Int,
        val isWarningBottomOption: Boolean = true,
    ) : ModalData() {
        data class FolderMore(
            val folderId: Long,
            val folderName: String,
        ) : OptionModal(
            topOption = R.string.modal_folder_name_edit_title,
            bottomOption = R.string.dialog_folder_delete_title,
        )

        object ImageSelection : OptionModal(
            topOption = R.string.modal_image_upload_camera,
            bottomOption = R.string.modal_image_upload_gallery,
            isWarningBottomOption = false,
        )
    }

    fun openModal(context: Context, resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val intent = Intent(
            context,
            ModalActivity::class.java,
        ).apply { putExtra(ModalActivity.ARG_MODAL_DATA, this@ModalData) }

        resultLauncher.launch(intent)
    }
}
