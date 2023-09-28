package com.hyeeyoung.wishboard.designsystem.component.dialog.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.designsystem.component.dialog.model.ModalData
import com.hyeeyoung.wishboard.presentation.folder.FolderListModalContent
import com.hyeeyoung.wishboard.presentation.folder.FolderUploadModalContent
import com.hyeeyoung.wishboard.presentation.noti.NotiModalContent
import com.hyeeyoung.wishboard.presentation.onboarding.OnboardingModalContent
import com.hyeeyoung.wishboard.presentation.upload.component.ShopLinkModalContent
import com.hyeeyoung.wishboard.presentation.util.extension.getSerializable

class ModalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modalData = intent.getSerializable(ARG_MODAL_DATA, ModalData::class.java)

        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                when (modalData) {
                    is ModalData.Modal -> systemUiController.setNavigationBarColor(Color.White)
                    is ModalData.OptionModal -> systemUiController.setNavigationBarColor(Color.Transparent)
                    else -> {}
                }
            }

            when (modalData) {
                is ModalData.Modal -> {
                    WishBoardModal(
                        titleRes = modalData.title,
                        onDismissRequest = { finish() },
                        content = { ModalContent(modalData = modalData) },
                    )
                }

                is ModalData.OptionModal -> {
                    WishBoardTwoOptionModal(
                        topOption = modalData.topOption,
                        bottomOption = modalData.bottomOption,
                        isWarningBottom = modalData.isWarningBottomOption,
                        onClickTop = { moveToPrevious(modalData) },
                        onClickBottom = { moveToPrevious(modalData, false) },
                        onDismissRequest = { finish() },
                    )
                }

                is ModalData.FullModal -> {
                    when (modalData) {
                        is ModalData.FullModal.Onboarding -> OnboardingModalContent(onDismissRequest = { finish() })
                    }
                }

                else -> {}
            }
        }
    }

    @Composable
    fun ModalContent(modalData: ModalData.Modal) {
        when (modalData) {
            is ModalData.Modal.FolderList -> FolderListModalContent(
                selectedFolderId = modalData.selectedFolderId,
                onClickFolder = { folder -> moveToPrevious(modalData.copy(selectedFolderId = folder.id, folder.name)) },
            )

            is ModalData.Modal.Noti -> NotiModalContent(
                type = modalData.notiType,
                date = modalData.notiDate,
                onClickComplete = {},
            )

            is ModalData.Modal.NewFolder -> FolderUploadModalContent(onClickComplete = { name ->
                moveToPrevious(modalData.copy(folderName = name))
            })

            is ModalData.Modal.FolderNameEdit -> FolderUploadModalContent(
                folder = Pair(
                    modalData.folderId,
                    modalData.folderName,
                ),
                onClickComplete = { name -> moveToPrevious(modalData.copy(folderName = name)) },
            )

            is ModalData.Modal.ShopLink -> ShopLinkModalContent(
                link = modalData.link,
                onClickComplete = { link -> moveToPrevious(modalData.copy(link = link)) },
            )
        }
    }

    /** 옵션 모달 전용 이전 화면으로 돌아가기 + 데이터 전달 */
    private fun moveToPrevious(modal: ModalData.OptionModal, isTop: Boolean = true) {
        Intent().apply {
            putExtra(ARG_MODAL_DATA, modal)
            putExtra(ARG_TOP_OPTION, isTop)
        }.also {
            setResult(RESULT_OK, it)
            finish()
        }
    }

    private fun moveToPrevious(modal: ModalData.Modal) {
        Intent().apply {
            putExtra(ARG_MODAL_DATA, modal)
        }.also {
            setResult(RESULT_OK, it)
            finish()
        }
    }

    companion object {
        const val ARG_MODAL_DATA = "modalData"
        const val ARG_TOP_OPTION = "isTopOption"
    }
}
