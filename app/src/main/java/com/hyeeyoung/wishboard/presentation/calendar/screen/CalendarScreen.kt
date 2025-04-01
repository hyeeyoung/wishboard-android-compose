package com.hyeeyoung.wishboard.presentation.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hyeeyoung.wishboard.config.navigation.screen.MainScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.noti.NotiType
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel.Companion.INITIAL_PAGE
import com.hyeeyoung.wishboard.presentation.calendar.CalendarViewModel.Companion.PAGE_COUNT
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarHeader
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarSchedule
import com.hyeeyoung.wishboard.presentation.calendar.component.CalendarTable
import com.hyeeyoung.wishboard.presentation.noti.model.CalendarUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.NotiItem
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    SideEffect {
        systemUiController.setNavigationBarColor(Color.White)
    }

    CalendarScreen(
        uiModel = uiModel,
        onClickSchedule = { id ->
            navController.navigate("${MainScreen.WishItemDetail.route}/$id")
        },
        updateSelectedDate = viewModel::updateSelectedDate,
        changeCalendarPage = viewModel::changeCalendarPage,
        onClickClose = navController::safePopBackStack
    )
}

@Composable
fun CalendarScreen(
    uiModel: CalendarUiModel,
    updateSelectedDate: (LocalDate) -> Unit,
    onClickSchedule: (id: Long) -> Unit,
    changeCalendarPage: (page: Int) -> Unit,
    onClickClose: () -> Unit,
) {
    val curMonthNoti by remember(uiModel.schedules, uiModel.selectedDate) {
        mutableStateOf(
            uiModel.schedules.filter {
                it.notiDate != null && it.notiDate.year == uiModel.selectedDate.year && it.notiDate.month == uiModel.selectedDate.month
            }
        )
    }
    val curDateNoti by remember(curMonthNoti, uiModel.selectedDate) {
        mutableStateOf(curMonthNoti.filter { it.notiDate?.dayOfMonth == uiModel.selectedDate.dayOfMonth })
    }
    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE, pageCount = { PAGE_COUNT })

    Scaffold(
        topBar = {
            CalendarHeader(
                selectedDate = uiModel.selectedDate,
                onClickClose = onClickClose,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(WishBoardTheme.colors.white)
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            CalendarTable(
                selectedDate = uiModel.selectedDate,
                onSelect = { date -> updateSelectedDate(date) },
                notiDateList = curMonthNoti.map { it.notiDate!!.date.toJavaLocalDate() },
                pagerState = pagerState,
                pageCount = PAGE_COUNT,
                onChangePage = { page -> changeCalendarPage(page) },
            )
            CalendarSchedule(
                selectedDate = uiModel.selectedDate,
                notiItems = curDateNoti,
                onClickSchedule = { id ->
                    onClickSchedule(id)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val schedules = listOf(
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            false,
            NotiType.RESTOCK,
            LocalDateTime(2023, 5, 27, 15, 0),
        ),
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            false,
            NotiType.RESTOCK,
            LocalDateTime(2023, 6, 7, 15, 0),
        ),
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            true,
            NotiType.RESTOCK,
            LocalDateTime(2023, 7, 3, 13, 30),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            false,
            NotiType.PREORDER,
            LocalDateTime(2023, 7, 20, 0, 0),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            false,
            NotiType.PREORDER,
            LocalDateTime(2023, 8, 10, 11, 0),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            false,
            NotiType.PREORDER,
            LocalDateTime(2023, 8, 11, 14, 0),
        ),
        NotiItem(
            2,
            "https://image.msscdn.net/images/goods_img/20230427/3267246/3267246_16825933559850_500.jpg",
            "체리 자카드 패턴 숏 슬리브 가디건 [핑크]",
            "https://www.musinsa.com/app/goods/3267246/0",
            false,
            NotiType.PREORDER,
            LocalDateTime(2023, 8, 22, 19, 0),
        ),
        NotiItem(
            1,
            "https://image.msscdn.net/images/goods_img/20220222/2377269/2377269_16777177260753_500.jpg",
            "W CLASSIC LOGO TEE white",
            "https://www.musinsa.com/app/goods/2377269",
            false,
            NotiType.RESTOCK,
            LocalDateTime(2024, 5, 18, 20, 0),
        ),
    )

    CalendarScreen(
        uiModel = CalendarUiModel(
            schedules = schedules,
            selectedDate = LocalDate.of(2025, 3, 23),
        ),
        onClickSchedule = {},
        updateSelectedDate = {},
        changeCalendarPage = {},
        onClickClose = {})
}
