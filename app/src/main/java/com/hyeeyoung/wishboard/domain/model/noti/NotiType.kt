package com.hyeeyoung.wishboard.domain.model.noti

import com.hyeeyoung.wishboard.domain.util.safeValueOf
import java.io.Serializable

enum class NotiType(val label: String) : Serializable {
    RESTOCK("재입고"),
    OPEN("오픈"),
    PREORDER("프리오더"),
    SALE_START("세일 시작"),
    SALE_END("세일 마감"),
    REMINDER("리마인드"),
    ;
    companion object {
        fun fromLabel(label: String) =
            when (label) {
                RESTOCK.label -> RESTOCK
                OPEN.label -> OPEN
                PREORDER.label -> PREORDER
                SALE_END.label -> SALE_END
                SALE_START.label -> SALE_START
                REMINDER.label -> REMINDER
                else -> throw IllegalArgumentException("유효하지 않은 알림 유형입니다.")
            }

        fun fromDomain(domain: String?) = safeValueOf<NotiType>(domain)
    }
}
