package com.hyeeyoung.wishboard.presentation.calendar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardIconButton
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarHeader(selectedDate: LocalDate, onClickBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CalendarTopBar(month = selectedDate.month, year = selectedDate.year, onClickBack = onClickBack)
        DayOfTheWeekLabel()
    }
}

/** 월, 년도 정보, 백버튼을 포함하는 캘린더 타이틀 라벨 */
@Composable
fun CalendarTopBar(month: Month, year: Int, onClickBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Spacer(modifier = Modifier.width(5.dp))
            WishBoardIconButton(iconRes = R.drawable.ic_back, onClick = { onClickBack() })
        }
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "${month.getDisplayName(TextStyle.FULL, Locale.US)} $year",
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.montserratH1,
        )
    }
}

/** 월 - 일요일까지의 모든 요일 라벨 */
@Composable
fun DayOfTheWeekLabel() {
    val days = listOf("Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 12.dp),
    ) {
        days.forEach { day ->
            Text(
                modifier = Modifier.weight(1f),
                text = day,
                color = WishBoardTheme.colors.gray700,
                textAlign = TextAlign.Center,
                style = WishBoardTheme.typography.montserratB2,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarHeaderPreview() {
    CalendarHeader(selectedDate = LocalDate.now(), onClickBack = {})
}
