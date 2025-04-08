package com.hyeeyoung.wishboard.presentation.noti

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.Picker
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.style.Gray100
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.noti.NotiInfo
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.domain.model.noti.NotiType.Companion.toNotiType
import com.hyeeyoung.wishboard.presentation.util.NumberPickerUtil
import com.hyeeyoung.wishboard.presentation.util.NumberPickerUtil.getFormattedNumberPickerDate
import com.hyeeyoung.wishboard.presentation.util.NumberPickerUtil.getFormattedNumberPickerTime
import kotlinx.datetime.LocalDateTime

private val notiType = NotiType.entries.map { it.label }

@Composable
fun NotiModalContent(
    notiInfo: NotiInfo,
    onClickComplete: (NotiType?, LocalDateTime?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val selectedType = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf("") }
    val selectedHour = remember { mutableStateOf("") }
    val selectedMinute = remember { mutableStateOf("") }

    val dateStartIndex = NumberPickerUtil.dates.indexOf(notiInfo.notiDate?.date?.getFormattedNumberPickerDate())
    val hourStartIndex = NumberPickerUtil.hours.indexOf(notiInfo.notiDate?.hour?.getFormattedNumberPickerTime())
    val minuteStartIndex = NumberPickerUtil.minutes.indexOf(notiInfo.notiDate?.minute?.getFormattedNumberPickerTime())

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
                text = stringResource(id = R.string.modal_noti_setting_title),
                style = WishBoardTheme.typography.suitH3,
                color = WishBoardTheme.colors.gray700,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 8.dp),
            ) {
                WishBoardIconButton(
                    iconRes = R.drawable.ic_close,
                    onClick = onDismissRequest,
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Box {
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
                    startIdx = notiInfo.notiType?.ordinal ?: 0,
                    selectedItem = selectedType,
                )

                Picker(
                    modifier = Modifier
                        .widthIn(max = 126.dp)
                        .align(Alignment.Center),
                    itemList = NumberPickerUtil.dates,
                    startIdx = if (dateStartIndex != -1) dateStartIndex else 0,
                    selectedItem = selectedDate,
                )

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Picker(
                        modifier = Modifier.widthIn(max = 40.dp),
                        itemList = NumberPickerUtil.hours,
                        startIdx = if (hourStartIndex != -1) hourStartIndex else 0,
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
                        itemList = NumberPickerUtil.minutes,
                        startIdx = if (minuteStartIndex != -1) minuteStartIndex else 0,
                        selectedItem = selectedMinute,
                        enableInfiniteScroll = false,
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 6.dp),
                text = stringResource(id = R.string.modal_noti_setting_guide),
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray300,
                textAlign = TextAlign.Center,
            )

            WishBoardWideButton(
                enabled = true,
                onClick = {
                    onClickComplete(
                        selectedType.value.toNotiType(),
                        NumberPickerUtil.toLocalDateTime(
                            date = selectedDate.value,
                            hour = selectedHour.value,
                            minute = selectedMinute.value
                        )
                    )
                },
                text = stringResource(id = R.string.complete)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewNotiModalContent() {
    NotiModalContent(
        notiInfo = NotiInfo(
            notiType = NotiType.OPEN,
            notiDate = LocalDateTime(25, 3, 25, 12, 30)
        ),
        onClickComplete = { _, _ -> },
        onDismissRequest = {},
    )
}
