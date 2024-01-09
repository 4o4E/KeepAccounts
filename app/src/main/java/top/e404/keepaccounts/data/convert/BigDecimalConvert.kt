package top.e404.keepaccounts.data.convert

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConvert {
    @TypeConverter
    fun BigDecimal.encodeToString() = toString()

    @TypeConverter
    fun String.decodeToBigDecimal() = BigDecimal(this)
}