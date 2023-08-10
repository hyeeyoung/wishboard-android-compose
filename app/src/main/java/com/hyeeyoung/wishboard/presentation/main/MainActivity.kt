package com.hyeeyoung.wishboard.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO 상태바 컬러 변경, 네비게이션 구현
            MainScreen()
        }
    }
}
