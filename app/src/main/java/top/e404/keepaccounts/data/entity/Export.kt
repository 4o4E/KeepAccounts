package top.e404.keepaccounts.data.entity

import kotlinx.serialization.Serializable
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.dao.RecordTag
import top.e404.keepaccounts.data.dao.Tag

@Serializable
data class Export(
    val record: List<BalanceRecord>,
    val tag: List<Tag>,
    val recordTag: List<RecordTag>
)