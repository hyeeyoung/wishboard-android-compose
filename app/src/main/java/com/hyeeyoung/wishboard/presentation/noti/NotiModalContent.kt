package com.hyeeyoung.wishboard.presentation.noti

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.Picker
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.style.Gray100
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import kotlinx.datetime.LocalDateTime

private val notiType = NotiType.values().map { it.str }

@Composable
fun NotiModalContent(type: NotiType? = null, date: LocalDateTime? = null, onClickComplete: () -> Unit) {
    val selectedType = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    val selectedHour = remember { mutableStateOf("") }
    val selectedMinute = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .height(34.dp),
                onDraw = { drawRoundRect(color = Gray100, cornerRadius = CornerRadius(6.dp.toPx())) },
            )
            Picker(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart)
                    .widthIn(max = 80.dp),
                itemList = notiType,
                startIdx = type?.ordinal ?: 0,
                selectedItem = selectedType,
            )
            Picker(
                modifier = Modifier
                    .widthIn(max = 126.dp)
                    .align(Alignment.Center),
                itemList = listOf("22년 2월 19일 토", "22년 12월 19일 토", "22년 10월 19일 토"), // TODO 실데이터 넣기
                selectedItem = selectedDate,
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Picker(
                    modifier = Modifier.widthIn(max = 40.dp),
                    itemList = listOf("1", "2", "3", "10", "12"),
                    selectedItem = selectedHour,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.modal_noti_setting_time_delimiter),
                    style = WishBoardTheme.typography.suitB3,
                    color = WishBoardTheme.colors.gray700,
                )
                Picker(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .widthIn(max = 40.dp),
                    itemList = listOf("00", "30"),
                    selectedItem = selectedMinute,
                    enableInfiniteScroll = false,
                )
            }
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 6.dp),
            text = stringResource(id = R.string.modal_noti_setting_guide),
            style = WishBoardTheme.typography.suitD3,
            color = WishBoardTheme.colors.gray300,
        )
        WishBoardWideButton(enabled = true, onClick = {
            onClickComplete()
        }, text = stringResource(id = R.string.complete))
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewNotiModalContent() {
    NotiModalContent(onClickComplete = {})
}
