package com.hyeeyoung.wishboard.config

import timber.log.Timber

class WishBoardDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement) =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
