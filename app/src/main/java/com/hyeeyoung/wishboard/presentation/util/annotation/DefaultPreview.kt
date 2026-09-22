package com.hyeeyoung.wishboard.presentation.util.annotation

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "1-normal-mode",
    device = "spec:width=360dp,height=800dp,dpi=480",
    showBackground = true,
    backgroundColor = 0xFFFFFF,
)
@Preview(
    name = "2-huge-font-mode",
    device = "spec:width=360dp,height=800dp,dpi=480",
    fontScale = 2.0f,
    showBackground = true,
    backgroundColor = 0xFFFFFF,
)
@Preview(
    name = "3-foldable-mode",
    device = "spec:width=673dp,height=841dp",
    showBackground = true,
    backgroundColor = 0xFFFFFF,
)
@Preview(
    name = "4-galaxy-fold8(16:10)",
    device = "spec:width=475dp,height=751dp",
    widthDp = 475,
    heightDp = 751,
    showBackground = true,
    backgroundColor = 0xFFFFFF,
)
@Preview(
    name = "5-galaxy-fold8(4:3)",
    device = "spec:width=932dp,height=704dp",
    widthDp = 932,
    heightDp = 704,
    showBackground = true,
    backgroundColor = 0xFFFFFF,
)
annotation class DefaultPreview
