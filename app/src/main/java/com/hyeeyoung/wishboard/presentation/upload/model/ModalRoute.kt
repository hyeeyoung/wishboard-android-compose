package com.hyeeyoung.wishboard.presentation.upload.model

import androidx.annotation.StringRes
import com.hyeeyoung.wishboard.R

enum class ModalRoute(@StringRes val titleRes: Int) {
    FOLDER(R.string.modal_folder_selection_title), NOTI(R.string.modal_noti_setting_title), SHOP(
        R.string.modal_shop_link_title,
    )
}
