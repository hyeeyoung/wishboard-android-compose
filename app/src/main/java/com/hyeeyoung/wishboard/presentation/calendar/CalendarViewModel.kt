package com.hyeeyoung.wishboard.presentation.calendar

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.core.extension.onFailure
import com.hyeeyoung.wishboard.domain.usecase.noti.GetAllNotiListUseCase
import com.hyeeyoung.wishboard.presentation.common.BaseViewModel
import com.hyeeyoung.wishboard.presentation.noti.model.CalendarUiModel
import com.hyeeyoung.wishboard.presentation.sign.model.snackbar.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getAllNotiListUseCase: GetAllNotiListUseCase,
) : BaseViewModel() {
    private val _uiModel = MutableStateFlow(CalendarUiModel())
    val uiModel = _uiModel.asStateFlow()
    private val latestCalendarPage = MutableStateFlow(INITIAL_PAGE)

    fun fetchSchedule() {
        viewModelScope.launch {
            getAllNotiListUseCase().onSuccess { schedules ->
                _uiModel.update {
                    it.copy(schedules = schedules)
                }
            }.onFailure { _, errorCode, _ ->
                when (errorCode) {
                    404 -> {
                        _uiModel.update { it.copy(schedules = emptyList()) }
                    }

                    else -> {
                        updateSnackbarMessage(SnackbarMessage.DEFAULT)
                    }
                }
            }
        }
    }

    fun changeCalendarPage(currentPate: Int) {
        val diff = currentPate - latestCalendarPage.value

        if (diff < 0) {
            _uiModel.update {
                it.copy(selectedDate = it.selectedDate.minusMonths(1))
            }
        } else if (diff > 0) {
            _uiModel.update {
                it.copy(selectedDate = it.selectedDate.plusMonths(1))
            }
        }

        latestCalendarPage.value = currentPate
    }

    fun updateSelectedDate(date: LocalDate) {
        _uiModel.update {
            it.copy(selectedDate = date)
        }
    }

    companion object {
        const val PAGE_COUNT = Int.MAX_VALUE
        const val INITIAL_PAGE = PAGE_COUNT / 2
    }
}
