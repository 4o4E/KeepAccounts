package top.e404.keepaccounts.util

import androidx.compose.runtime.mutableLongStateOf

object Update {
    var record = mutableLongStateOf(0L)
    var tag = mutableLongStateOf(0L)
}