package com.hyeeyoung.wishboard.presentation.util.extension

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.hyeeyoung.wishboard.presentation.dialog.ModalActivity
import com.hyeeyoung.wishboard.presentation.dialog.ModalData
import java.io.Serializable

fun <T : Serializable> Intent.getSerializable(name: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(name, clazz)
    } else {
        getSerializableExtra(name) as? T ?: throw NullPointerException("타입 변환 실패")
    }
}

@Composable
fun rememberModalLauncher(afterModalDataInput: (Boolean, ModalData?) -> Unit) =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        it.data?.let { intent ->
            val isTopOption = intent.getBooleanExtra(ModalActivity.ARG_TOP_OPTION, false)
            val data = intent.getSerializable(ModalActivity.ARG_MODAL_DATA, ModalData::class.java)
            afterModalDataInput(isTopOption, data)
        }
    }
