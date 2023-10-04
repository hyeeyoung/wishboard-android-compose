package com.hyeeyoung.wishboard.presentation.calendar.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.designsystem.style.WishboardTheme
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel.Companion.INITIAL_PAGE
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel.Companion.PAGE_COUNT
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarHeader
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarSchedule
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarTable
import com.hyeeyoung.wishboard.presentation.model.NotiItem
import com.hyeeyoung.wishboard.presentation.util.type.NotiType
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(navController: NavHostController, viewModel: CalendarViewModel = viewModel()) {
    // TODO 서버 연동 후 삭제
    val notiList = listOf(
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            0,
            NotiType.RESTOCK,
            LocalDateTime.of(2023, 5, 27, 15, 0),
        ),
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            0,
            NotiType.RESTOCK,
            LocalDateTime.of(2023, 6, 7, 15, 0),
        ),
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
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            0,
            NotiType.PREORDER,
            LocalDateTime.of(2023, 8, 10, 11, 0),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            0,
            NotiType.PREORDER,
            LocalDateTime.of(2023, 8, 11, 14, 0),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            0,
            NotiType.PREORDER,
            LocalDateTime.of(2023, 8, 22, 19, 0),
        ),
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            0,
            NotiType.RESTOCK,
            LocalDateTime.of(2024, 5, 18, 20, 0),
        ),
    )

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(Color.Transparent)
    }

    WishboardTheme {
        val selectedDate = viewModel.selectedDate.collectAsState()
        val curMonthNoti =
            notiList.filter {
                it.notiDate.year == selectedDate.value.year && it.notiDate.month == selectedDate.value.month
            }
        val curDateNoti = curMonthNoti.filter { it.notiDate.dayOfMonth == selectedDate.value.dayOfMonth }
        val pagerState = rememberPagerState(initialPage = INITIAL_PAGE)

        Scaffold(
            topBar = {
                CalendarHeader(
                    selectedDate = selectedDate.value,
                    onClickBack = { navController.popBackStack() },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .padding(top = paddingValues.calculateTopPadding()),
            ) {
                CalendarTable(
                    selectedDate = selectedDate.value,
                    onSelect = { date -> viewModel.updateSelectedDate(date) },
                    notiDateList = curMonthNoti.map { it.notiDate.toLocalDate() },
                    pagerState = pagerState,
                    pageCount = PAGE_COUNT,
                    onChangePage = { page -> viewModel.changeCalendarPage(page) },
                )
                CalendarSchedule(
                    selectedDate = selectedDate.value,
                    notiItems = curDateNoti,
                    onClickSchedule = { id ->
                        navController.navigate("${MainScreen.WishItemDetail.route}/$id")
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    CalendarScreen(navController = rememberNavController())
}
