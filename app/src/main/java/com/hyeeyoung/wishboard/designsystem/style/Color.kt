package com.hyeeyoung.wishboard.designsystem.style

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Green Scale
val Green200 = Color(0xFFE5FEE7)
val Green500 = Color(0xFF95FB9D)
val Green700 = Color(0xFF68EB72)

// Pink Scale
val Pink700 = Color(0xFFFA5DBB)

// Gray Scale
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Gray50 = Color(0xFFF8F8F8)
val Gray100 = Color(0xFFEBEBEB)
val Gray150 = Color(0xFFCACACA)
val Gray200 = Color(0xFFAAAAAA)
val Gray300 = Color(0xFF858585)
val Gray600 = Color(0xFF474747)
val Gray700 = Color(0xFF292929)

val BlackAlpha5 = Color(0x0D000000)

@Stable
class WishBoardColors(
    green200: Color,
    green500: Color,
    green700: Color,
    pink700: Color,
    white: Color,
    black: Color,
    gray50: Color,
    gray100: Color,
    gray150: Color,
    gray200: Color,
    gray300: Color,
    gray600: Color,
    gray700: Color,
    blackAlpha5: Color,
) {
    var green200 by mutableStateOf(green200)
        private set
    var green500 by mutableStateOf(green500)
        private set
    var green700 by mutableStateOf(green700)
        private set
    var pink700 by mutableStateOf(pink700)
        private set
    var white by mutableStateOf(white)
        private set
    var black by mutableStateOf(black)
        private set
    var gray50 by mutableStateOf(gray50)
        private set
    var gray100 by mutableStateOf(gray100)
        private set
    var gray150 by mutableStateOf(gray150)
        private set
    var gray200 by mutableStateOf(gray200)
        private set
    var gray300 by mutableStateOf(gray300)
        private set
    var gray600 by mutableStateOf(gray600)
        private set
    var gray700 by mutableStateOf(gray700)
        private set

    var blackAlpha5 by mutableStateOf(blackAlpha5)
        private set

    fun copy(
        green200: Color = this.green200,
        green500: Color = this.green500,
        green700: Color = this.green700,
        pink700: Color = this.pink700,
        white: Color = this.white,
        black: Color = this.black,
        gray50: Color = this.gray50,
        gray100: Color = this.gray100,
        gray150: Color = this.gray150,
        gray200: Color = this.gray200,
        gray300: Color = this.gray300,
        gray600: Color = this.gray600,
        gray700: Color = this.gray700,
        blackAlpha5: Color = this.blackAlpha5,
    ) = WishBoardColors(
        green200 = green200,
        green500 = green500,
        green700 = green700,
        pink700 = pink700,
        white = white,
        black = black,
        gray50 = gray50,
        gray100 = gray100,
        gray150 = gray150,
        gray200 = gray200,
        gray300 = gray300,
        gray600 = gray600,
        gray700 = gray700,
        blackAlpha5 = blackAlpha5,
    )

    fun update(other: WishBoardColors) {
        green200 = other.green200
        green500 = other.green500
        green700 = other.green700
        pink700 = other.pink700
        gray50 = other.gray50
        gray100 = other.gray100
        gray150 = other.gray150
        gray200 = other.gray200
        gray300 = other.gray300
        gray600 = other.gray600
        gray700 = other.gray700
        blackAlpha5 = other.blackAlpha5
    }
}

fun wishBoardLightColors(
    green200: Color = Green200,
    green500: Color = Green500,
    green700: Color = Green700,
    pink700: Color = Pink700,
    white: Color = White,
    black: Color = Black,
    gray50: Color = Gray50,
    gray100: Color = Gray100,
    gray150: Color = Gray150,
    gray200: Color = Gray200,
    gray300: Color = Gray300,
    gray600: Color = Gray600,
    gray700: Color = Gray700,
    blackAlpha5: Color = BlackAlpha5,
) = WishBoardColors(
    green200,
    green500,
    green700,
    pink700,
    white,
    black,
    gray50,
    gray100,
    gray150,
    gray200,
    gray300,
    gray600,
    gray700,
    blackAlpha5,
)
