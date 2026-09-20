package com.hyeeyoung.wishboard.config

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalState {
    val autoLoginExpiryInfo: MutableStateFlow<Pair<Boolean, String>> = MutableStateFlow(false to "") // TODO 데이터타입 변경 필요
    var previousBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)
    var reselectedBottomBarRoute: MutableStateFlow<String?> = MutableStateFlow(null)

    // 현재 활성화된 화면이 아이템 선택 모드에 진입하면 값이 채워지고, 하단 네비게이션 바 대신 선택 모드 액션 바가 노출된다
    val bottomBarSelectionModeState: MutableStateFlow<BottomBarSelectionModeState?> = MutableStateFlow(null)
}

data class BottomBarSelectionModeState(
    val selectedItemCount: Int,
    val isAllSelected: Boolean,
    val onClickSelectAll: () -> Unit,
    val onClickDelete: () -> Unit,
)
