package top.e404.keepaccounts.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * 记账数据
 *
 * @property record 记录
 * @property tag tag
 */
@Serializable
@Entity(tableName = RecordTag.TABLE_NAME, primaryKeys = ["record", "tag"])
data class RecordTag(
    var record: Int,
    var tag: Int,
) {
    companion object {
        const val TABLE_NAME = "record_tag"
    }
}

@Dao
interface RecordTagDao {
    @Query("""SELECT * FROM ${RecordTag.TABLE_NAME}""")
    fun flow(): Flow<RecordTag>

    @Query("""SELECT * FROM ${RecordTag.TABLE_NAME}""")
    fun list(): List<RecordTag>

    @Query(
        """
        SELECT t.*
        FROM ${Tag.TABLE_NAME} t
        ORDER BY (SELECT COUNT(*) FROM ${RecordTag.TABLE_NAME} rt WHERE rt.tag = t.tag) DESC
        """
    )
    fun listByUsedDegree(): Flow<List<Tag>>

    @Query(
        """
        SELECT t.* 
        FROM ${Tag.TABLE_NAME} t
        WHERE t.id in (SELECT rt.tag FROM ${RecordTag.TABLE_NAME} rt WHERE rt.record = :record)
        """
    )
    fun flowByRecord(record: Int): Flow<List<Tag>>

    @Query(
        """
        SELECT t.* 
        FROM ${Tag.TABLE_NAME} t
        WHERE t.id in (SELECT rt.tag FROM ${RecordTag.TABLE_NAME} rt WHERE rt.record = :record)
        """
    )
    fun listByRecord(record: Int): List<Tag>

    @Insert
    fun insert(vararg change: RecordTag)

    @Insert
    fun insert(change: Collection<RecordTag>)

    @Update
    fun update(change: RecordTag)

    @Delete
    fun delete(change: RecordTag)

    @Query("DELETE FROM ${RecordTag.TABLE_NAME} WHERE record = :recordId")
    fun deleteByRecord(recordId: Int)

    @Transaction
    fun updateByRecord(recordId: Int, tags: List<Int>) {
        deleteByRecord(recordId)
        insert(tags.map { RecordTag(recordId, it) })
    }

    @Query("DELETE FROM ${RecordTag.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun importData(tags: List<RecordTag>) {
        deleteAll()
        insert(tags)
    }
}