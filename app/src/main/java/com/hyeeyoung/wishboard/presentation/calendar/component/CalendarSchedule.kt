package com.hyeeyoung.wishboard.presentation.calendar.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.style.Gray700
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.NotiItem
import com.hyeeyoung.wishboard.presentation.util.extension.getScheduleTimeFormat
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun CalendarSchedule(
    selectedDate: LocalDate,
    notiItems: List<NotiItem>,
    onClickNotiWithLink: (String) -> Unit,
    onClickNotiWithoutLink: () -> Unit,
) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
        Text(
            text = stringResource(
                id = R.string.calendar_schedule_title,
                formatArgs = arrayOf(selectedDate.monthValue, selectedDate.dayOfMonth),
            ),
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitH3,
        )
        if (notiItems.isEmpty()) {
            EmptySchedule(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .weight(1.0f),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(notiItems) { noti ->
                    ScheduleItem(
                        noti = noti,
                        onClickNotiWithLink = onClickNotiWithLink,
                        onClickNotiWithoutLink,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySchedule(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_noti_large),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.noti_no_item_view),
            textAlign = TextAlign.Center,
            color = WishBoardTheme.colors.gray200,
            style = WishBoardTheme.typography.suitB3M,
        )
    }
}

@Composable
fun ScheduleItem(
    noti: NotiItem,
    onClickNotiWithLink: (String) -> Unit,
    onClickNotiWithoutLink: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                if (noti.itemUrl.isNullOrEmpty()) {
                    onClickNotiWithoutLink()
                } else {
                    onClickNotiWithLink(noti.itemUrl)
                }
            }
            .background(color = WishBoardTheme.colors.gray50, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        val (image, notiType, notiDate, itemName) = createRefs()
        ColoredImage(
            modifier = Modifier
                .constrainAs(image) { start.linkTo(parent.start) }
                .size(72.dp)
                .clip(CircleShape),
            model = noti.itemImg,
        )
        Text(
            modifier = Modifier.constrainAs(notiType) {
                start.linkTo(image.end, margin = 10.dp)
                top.linkTo(parent.top)
            },
            text = stringResource(id = R.string.noti_item_type, noti.notiType.str),
            color = WishBoardTheme.colors.gray700,
            style = WishBoardTheme.typography.suitH5,
        )
        Text(
            modifier = Modifier
                .constrainAs(itemName) {
                    width = Dimension.fillToConstraints
                    top.linkTo(notiType.bottom, margin = 6.dp)
                    start.linkTo(notiType.start)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            text = noti.itemName,
            color = Gray700,
            style = WishBoardTheme.typography.suitD3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier.constrainAs(notiDate) {
                bottom.linkTo(parent.bottom)
                start.linkTo(notiType.start)
            },
            text = noti.notiDate.getScheduleTimeFormat(),
            color = WishBoardTheme.colors.gray200,
            style = WishBoardTheme.typography.suitD3,
        )
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun CalendarEmptySchedulePreview() {
    CalendarSchedule(
        selectedDate = LocalDate.now(),
        notiItems = emptyList(),
        onClickNotiWithLink = {},
        onClickNotiWithoutLink = {},
    )
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun CalendarSchedulePreview() {
    CalendarSchedule(
        selectedDate = LocalDate.now(),
        listOf(
            NotiItem(
                1,
                "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
                "W CLASSIC LOGO TEE white",
                "https://www.musinsa.com/app/goods/2377269",
                0,
                NotiType.RESTOCK,
                LocalDateTime.of(2023, 7, 3, 13, 30),
            ),
            NotiItem(
                2,
                "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
                "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
                "https://www.musinsa.com/app/goods/3267246/0",
                0,
                NotiType.PREORDER,
                LocalDateTime.of(2023, 7, 20, 0, 0),
            ),
        ),
        onClickNotiWithLink = {},
        onClickNotiWithoutLink = {},
    )
}
