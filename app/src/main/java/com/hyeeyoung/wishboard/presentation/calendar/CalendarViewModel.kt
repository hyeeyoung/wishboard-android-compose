package com.hyeeyoung.wishboard.presentation.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate get() = _selectedDate.asStateFlow()

    private val latestCalendarPage = MutableStateFlow(INITIAL_PAGE)

    fun changeCalendarPage(currentPate: Int) {
        val diff = currentPate - latestCalendarPage.value

        if (diff < 0) {
            _selectedDate.value = _selectedDate.value.minusMonths(1)
        } else if (diff > 0) {
            _selectedDate.value = _selectedDate.value.plusMonths(1)
        }

        latestCalendarPage.value = currentPate
    }

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    companion object {
        const val PAGE_COUNT = Int.MAX_VALUE
        const val INITIAL_PAGE = PAGE_COUNT / 2
    }
}
