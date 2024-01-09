package top.e404.keepaccounts.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Stable
fun Color.contrastTextColor(): Color =
    if (luminance() > 0.5) Color.Black else Color.White