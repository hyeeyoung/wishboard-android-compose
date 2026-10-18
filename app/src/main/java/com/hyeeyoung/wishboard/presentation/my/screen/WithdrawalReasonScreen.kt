package com.hyeeyoung.wishboard.presentation.my.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.config.navigation.screen.SignScreen
import com.hyeeyoung.wishboard.designsystem.component.WishBoardGlobalSnackbarMessage
import com.hyeeyoung.wishboard.designsystem.component.button.WishBoardWideButton
import com.hyeeyoung.wishboard.designsystem.component.loading.ThreeDotsLoadingView
import com.hyeeyoung.wishboard.designsystem.component.topbar.WishBoardTopBar
import com.hyeeyoung.wishboard.designsystem.style.WishBoardTheme
import com.hyeeyoung.wishboard.domain.model.user.WithdrawalReason
import com.hyeeyoung.wishboard.presentation.my.WithdrawalReasonUiState
import com.hyeeyoung.wishboard.presentation.my.WithdrawalReasonViewModel
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardState
import com.hyeeyoung.wishboard.presentation.sign.model.WishBoardTopBarModel
import com.hyeeyoung.wishboard.presentation.util.extension.noRippleClickable
import com.hyeeyoung.wishboard.presentation.util.extension.safePopBackStack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun WithdrawalReasonScreen(
    navController: NavController,
    viewModel: WithdrawalReasonViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WishBoardGlobalSnackbarMessage(snackbarChannel = viewModel.snackBarChannel)

    BackHandler {
        navController.safePopBackStack()
    }

    WithdrawalReasonScreen(
        uiState = uiState,
        onSelectReason = viewModel::onSelectReason,
        onEtcTextChange = viewModel::onEtcTextChange,
        onClickWithdraw = {
            viewModel.deleteAccount {
                navController.navigate(SignScreen.Main.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        },
        onClickBack = { navController.safePopBackStack() },
    )
}

@Composable
fun WithdrawalReasonScreen(
    uiState: WithdrawalReasonUiState,
    onSelectReason: (WithdrawalReason) -> Unit,
    onEtcTextChange: (String) -> Unit,
    onClickWithdraw: () -> Unit,
    onClickBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            WishBoardTopBar(
                topBarModel = WishBoardTopBarModel(onClickStartIcon = onClickBack),
            )
        },
    ) { paddingValues ->

        if (uiState.fetchReasonsState == WishBoardState.Loading) {
            Box(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxSize()
                    .background(WishBoardTheme.colors.white),
                contentAlignment = Alignment.Center,
            ) {
                ThreeDotsLoadingView()
            }
        } else {
            Column(
                modifier = Modifier
                    .background(WishBoardTheme.colors.white)
                    .imePadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = paddingValues.calculateTopPadding())
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = dimensionResource(R.dimen.spacing_base),
                            end = dimensionResource(R.dimen.spacing_base),
                            top = 24.dp,
                            bottom = 80.dp,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.withdrawal_reason_title),
                        style = WishBoardTheme.typography.suitH0,
                        color = WishBoardTheme.colors.gray700,
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = stringResource(R.string.withdrawal_reason_subtitle),
                        style = WishBoardTheme.typography.suitD2M,
                        color = WishBoardTheme.colors.gray300,
                    )

                    Spacer(modifier = Modifier.size(32.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.reasons.forEach { reason ->
                            ReasonItem(
                                reason = reason,
                                isSelected = uiState.selectedReason?.code == reason.code,
                                onClick = {
                                    if (!reason.detailRequired) focusManager.clearFocus()
                                    onSelectReason(reason)
                                },
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.selectedReason?.detailRequired == true,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        Column {
                            Spacer(modifier = Modifier.size(12.dp))
                            EtcTextField(
                                text = uiState.etcText,
                                onTextChange = onEtcTextChange,
                                maxLength = WithdrawalReasonViewModel.MAX_ETC_LENGTH,
                            )
                        }
                    }
                }

                WishBoardWideButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = dimensionResource(R.dimen.spacing_base), vertical = 12.dp),
                    enabled = uiState.isButtonEnabled,
                    state = uiState.deleteAccountState,
                    onClick = onClickWithdraw,
                    text = stringResource(R.string.withdrawal_reason_button),
                )
            }
        }
    }
}

@Composable
private fun ReasonItem(
    reason: WithdrawalReason,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReasonCheckCircle(isSelected = isSelected)
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = reason.description,
            style = WishBoardTheme.typography.suitB3M,
            color = WishBoardTheme.colors.black,
        )
    }
}

@Composable
private fun ReasonCheckCircle(isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(WishBoardTheme.colors.green700, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_white),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = WishBoardTheme.colors.white,
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(18.dp)
                .border(1.dp, WishBoardTheme.colors.gray150, CircleShape),
        )
    }
}

@Composable
private fun EtcTextField(
    text: String,
    onTextChange: (String) -> Unit,
    maxLength: Int,
) {
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current

    // 입력란이 노출되면 포커스를 주고 키보드를 올린 뒤, 키보드 위로 입력란이 보이도록 스크롤한다.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        withTimeoutOrNull(KEYBOARD_WAIT_MILLIS) {
            snapshotFlow { imeInsets.getBottom(density) }.first { it > 0 }
        }
        bringIntoViewRequester.bringIntoView()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
            .background(WishBoardTheme.colors.gray50, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
    ) {
        BasicTextField(
            value = text,
            onValueChange = { if (it.length <= maxLength) onTextChange(it) },
            textStyle = WishBoardTheme.typography.suitD1.copy(color = WishBoardTheme.colors.gray700),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester),
        ) { innerTextField ->
            Box {
                if (text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.withdrawal_reason_etc_placeholder),
                        style = WishBoardTheme.typography.suitD1,
                        color = WishBoardTheme.colors.gray300,
                    )
                }
                innerTextField()
            }
        }

        // 입력 텍스트가 글자 수 표시 영역 아래로 내려가지 않도록 간격을 둔다.
        Spacer(modifier = Modifier.size(15.dp))

        Text(
            text = "(${text.length}/$maxLength)자",
            style = WishBoardTheme.typography.suitD3,
            color = WishBoardTheme.colors.gray200,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

private const val KEYBOARD_WAIT_MILLIS = 1000L
