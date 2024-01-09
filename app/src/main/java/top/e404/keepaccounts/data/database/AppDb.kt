package top.e404.keepaccounts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.dao.BalanceRecordDao
import top.e404.keepaccounts.data.dao.RecordTag
import top.e404.keepaccounts.data.dao.RecordTagDao
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.data.dao.TagDao

@Database(
    entities = [
        BalanceRecord::class,
        Tag::class,
        RecordTag::class
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val record: BalanceRecordDao
    abstract val recordTag: RecordTagDao
    abstract val tag: TagDao
}