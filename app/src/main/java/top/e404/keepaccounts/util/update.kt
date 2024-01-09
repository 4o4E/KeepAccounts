package top.e404.keepaccounts.util

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.data.query.RecordQuery

object ViewModel {
    val recordList = mutableStateOf(listOf<BalanceRecord>())
    val recordFilterList = mutableStateOf(listOf<BalanceRecord>())
    val tagList = mutableStateOf(listOf<Tag>())

    fun updateRecordList() = App.launch(Dispatchers.IO) {
        recordList.value = listOf()
        recordList.value = App.db.record.list()
    }

    fun updateRecordFilterList(query: RecordQuery) = App.launch(Dispatchers.IO) {
        recordFilterList.value = listOf()
        recordFilterList.value = App.db.record.query(query)
    }

    fun updateTagList() = App.launch(Dispatchers.IO) {
        tagList.value = listOf()
        tagList.value = App.db.tag.list()
    }
}