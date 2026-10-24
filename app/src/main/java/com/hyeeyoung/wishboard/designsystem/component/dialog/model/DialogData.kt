package com.hyeeyoung.wishboard.designsystem.component.dialog.model

import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardDialogTextRes
import java.io.Serializable

sealed class DialogData(
    val dialogTextRes: WishBoardDialogTextRes,
    val isWarningDialog: Boolean = true,
    val descriptionFormatArgs: List<Any> = emptyList(),
) : Serializable {
    data class WishItemDelete(
        val itemId: Long? = null,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_item_delete_title,
            descriptionRes = R.string.dialog_item_delete_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.delete,
        ),
    )

    data class BulkWishItemDelete(
        val itemCount: Int,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_item_delete_title,
            descriptionRes = R.string.dialog_bulk_item_delete_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.delete,
        ),
        descriptionFormatArgs = listOf(itemCount),
    )

    data class BulkWishItemRemoveFromFolder(
        val itemCount: Int,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_remove_from_folder_title,
            descriptionRes = R.string.dialog_bulk_remove_from_folder_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.remove,
        ),
        descriptionFormatArgs = listOf(itemCount),
    )

    data class BulkWishItemToWish(
        val itemCount: Int,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_bulk_convert_to_wish_title,
            descriptionRes = R.string.dialog_bulk_convert_to_wish_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.convert,
        ),
        isWarningDialog = false,
        descriptionFormatArgs = listOf(itemCount),
    )

    data class BulkWishItemToOwned(
        val itemCount: Int,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_bulk_convert_to_owned_title,
            descriptionRes = R.string.dialog_bulk_convert_to_owned_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.convert,
        ),
        isWarningDialog = false,
        descriptionFormatArgs = listOf(itemCount),
    )

    data class FolderDelete(
        val folderId: Long? = null,
    ) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_folder_delete_title,
            descriptionRes = R.string.dialog_folder_delete_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.delete,
        ),
    )

    data class CartDelete(val itemId: Long? = null) : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_cart_title,
            descriptionRes = R.string.dialog_cart_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.delete,
        ),
    )

    data object AppUpdate : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_update_title,
            descriptionRes = R.string.dialog_update_description,
            dismissBtnTextRes = R.string.later,
            confirmBtnTextRes = R.string.dialog_update_confirm_btn_text,
        ),
        isWarningDialog = false,
    )

    data object Logout : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.my_menu_logout,
            descriptionRes = R.string.dialog_logout_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.my_menu_logout,
        ),
    )

    data object OverwriteParsedItem : DialogData(
        dialogTextRes = WishBoardDialogTextRes(
            titleRes = R.string.dialog_overwrite_parsed_item_title,
            descriptionRes = R.string.dialog_overwrite_parsed_item_description,
            dismissBtnTextRes = R.string.cancel,
            confirmBtnTextRes = R.string.item_load_btn_text,
        ),
    )
}
