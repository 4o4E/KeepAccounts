package top.e404.keepaccounts.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import top.e404.keepaccounts.data.convert.BigDecimalConvert
import top.e404.keepaccounts.data.query.RecordQuery
import top.e404.keepaccounts.serializer.BigDecimalSerializer
import top.e404.keepaccounts.ui.component.record.RecordType
import java.math.BigDecimal


/**
 * 记账数据
 *
 * @property id 自增主键
 * @property desc 备注
 * @property value 支出数值
 * @property time 支出数值
 */
@Serializable
@Entity(tableName = BalanceRecord.TABLE_NAME)
@TypeConverters(BigDecimalConvert::class, RecordType.Converter::class)
data class BalanceRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @Serializable(RecordType.Serializer::class)
    var type: RecordType,
    var desc: String,
    @Serializable(BigDecimalSerializer::class)
    var value: BigDecimal,
    var time: Long,
) {
    companion object {
        const val TABLE_NAME = "balance_record"
    }
}

@Dao
interface BalanceRecordDao {

    @Query("SELECT * FROM ${BalanceRecord.TABLE_NAME} ORDER BY time DESC")
    fun list(): List<BalanceRecord>

    fun query(query: RecordQuery): List<BalanceRecord> {
        val sql = buildString {
            append("SELECT * FROM ${BalanceRecord.TABLE_NAME} WHERE true ")
            // time
            when {
                query.timeStart != null && query.timeEnd != null -> " AND `time` BETWEEN ${query.timeStart} AND ${query.timeEnd} "
                query.timeStart == null && query.timeEnd == null -> null
                query.timeStart != null -> " AND `time` > ${query.timeStart} "
                else -> " AND `time` < ${query.timeEnd} "
            }?.let { append(it) }
            // value
            when {
                query.valueStart != null && query.valueEnd != null -> " AND CAST(`value` AS REAL) BETWEEN ${query.valueStart} AND ${query.valueEnd} "
                query.valueStart == null && query.valueEnd == null -> null
                query.valueStart != null -> " AND CAST(`value` AS REAL) > ${query.valueStart} "
                else -> " AND CAST(`value` AS REAL) < ${query.valueEnd} "
            }?.let { append(it) }
            // desc
            if (query.desc != null) append(
                " AND `desc` LIKE '%${
                    query.desc!!.replace(
                        "'",
                        "''"
                    )
                }%' "
            )
            append(" ORDER BY `${query.order.value}` ${if (query.asc) "ASC" else "DESC"}")
        }
        return queryRaw(SimpleSQLiteQuery(sql))
    }

    @RawQuery
    fun queryRaw(query: SupportSQLiteQuery): List<BalanceRecord>

    @Transaction
    fun insertReturnId(record: BalanceRecord): Int {
        insert(record)
        return lastInsertId()
    }

    @Query("SELECT last_insert_rowid() FROM ${BalanceRecord.TABLE_NAME}")
    fun lastInsertId(): Int

    @Query("SELECT * FROM ${BalanceRecord.TABLE_NAME} WHERE time BETWEEN :start AND :end")
    fun listByTime(start: Long, end: Long): Flow<List<BalanceRecord>>

    @Insert
    fun insert(record: BalanceRecord)

    @Insert
    fun insert(records: Collection<BalanceRecord>)

    @Update
    fun update(record: BalanceRecord)

    @Delete
    fun delete(record: BalanceRecord)

    @Query("SELECT COUNT(*) FROM ${BalanceRecord.TABLE_NAME}")
    fun count(): Int

    @Query("DELETE FROM ${BalanceRecord.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun importData(record: Collection<BalanceRecord>) {
        deleteAll()
        insert(record)
    }

    @Query("SELECT * FROM ${BalanceRecord.TABLE_NAME} WHERE id = :id")
    fun flowById(id: Int): Flow<BalanceRecord>
}