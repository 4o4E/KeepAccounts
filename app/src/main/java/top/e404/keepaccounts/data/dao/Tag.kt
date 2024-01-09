package top.e404.keepaccounts.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * 记账数据tag
 *
 * @property id 自增主键
 * @property tag tag显示
 * @property desc tag备注
 * @property color 颜色
 */
@Serializable
@Entity(tableName = Tag.TABLE_NAME)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var tag: String,
    var desc: String,
    var color: Int
) {
    companion object {
        const val TABLE_NAME = "tag"
    }
}

@Dao
interface TagDao {
    @Query("SELECT * FROM ${Tag.TABLE_NAME}")
    fun flow(): Flow<List<Tag>>

    @Query("SELECT * FROM ${Tag.TABLE_NAME}")
    fun list(): List<Tag>

    @Insert
    fun insert(vararg tags: Tag)

    @Insert
    fun insert(tags: Collection<Tag>)

    @Update
    fun update(tag: Tag)

    @Delete
    fun delete(tag: Tag)

    @Query("DELETE FROM ${Tag.TABLE_NAME}")
    fun deleteAll()

    @Transaction
    fun importData(tags: List<Tag>) {
        deleteAll()
        insert(tags)
    }
}