package com.hyeeyoung.wishboard.domain.model.noti

import java.io.Serializable

enum class NotiType(val label: String): Serializable {
    RESTOCK("재입고"),
    OPEN("오픈"),
    PREORDER("프리오더"),
    SALE_START("세일 시작"),
    SALE_CLOSE("세일 마감"),
    REMIND("리마인드"),
    ;
    companion object {
        fun String.toNotiType() =
            when (this) {
                RESTOCK.label -> RESTOCK
                OPEN.label -> OPEN
                PREORDER.label -> PREORDER
                SALE_CLOSE.label -> SALE_CLOSE
                SALE_START.label -> SALE_START
                REMIND.label -> REMIND
                else -> throw IllegalArgumentException("유효하지 않은 알림 유형입니다.")
            }
    }
}
