package top.e404.keepaccounts.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import top.e404.keepaccounts.data.convert.BigDecimalConvert
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
    @Query("SELECT * FROM ${BalanceRecord.TABLE_NAME}")
    fun flow(): Flow<List<BalanceRecord>>

    @Query("SELECT * FROM ${BalanceRecord.TABLE_NAME}")
    fun list(): List<BalanceRecord>

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
}