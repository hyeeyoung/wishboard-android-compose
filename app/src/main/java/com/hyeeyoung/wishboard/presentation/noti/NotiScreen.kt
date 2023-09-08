package com.hyeeyoung.wishboard.presentation.noti

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.designsystem.component.ColoredImage
import com.hyeeyoung.wishboard.designsystem.component.divider.WishBoardDivider
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardMainTopBar
import com.hyeeyoung.wishboard.designsystem.style.Green500
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.presentation.model.Noti
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDateTime

@Composable
fun NotiScreen() {
    val noti = listOf(
        Noti(
            itemId = 1L,
            itemName = "Bean Ring Gold",
            itemImage = "https://url.kr/8vwf1e",
            type = NotiType.RESTOCK,
            date = LocalDateTime.now(),
            false,
        ),
    )
    val notiList = List(7) { noti }.flatten()

    Scaffold(topBar = {
        WishBoardMainTopBar(titleRes = R.string.noti)
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            itemsIndexed(notiList) { idx, item ->
                NotiItem(noti = item)
                if (idx < notiList.lastIndex) WishBoardDivider()
            }
        }
    }
}

@Composable
fun NotiItem(noti: Noti) {
    val imageSize = 80
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        ColoredImage(
            model = noti.itemImage,
            modifier = Modifier
                .size(imageSize.dp)
                .clip(CircleShape),
        )
        Column(
            modifier = Modifier
                .height(imageSize.dp)
                .padding(start = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${noti.type.str} ${stringResource(id = R.string.noti)}",
                    style = WishBoardTheme.typography.suitH5,
                    color = WishBoardTheme.colors.gray700,
                )
                if (!noti.isRead) Canvas(modifier = Modifier.size(8.dp), onDraw = { drawCircle(color = Green500) })
            }

            Text(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .weight(1f),
                text = noti.itemName,
                style = WishBoardTheme.typography.suitD3M,
                color = WishBoardTheme.colors.gray700,
            )
            Text(
                text = noti.date.toString(), // TODO 시간 포맷 적용
                style = WishBoardTheme.typography.suitD3,
                color = WishBoardTheme.colors.gray200,
            )
        }
    }
}

@Composable
@Preview
fun PreviewNotiScreen() {
    NotiScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewNotiItem() {
    val noti = Noti(
        itemId = 1L,
        itemName = "Bean Ring Gold",
        itemImage = "https://url.kr/8vwf1e",
        type = NotiType.RESTOCK,
        date = LocalDateTime.now(),
        false,
    )

    Column() {
        NotiItem(
            noti = noti,
        )
    }
}
