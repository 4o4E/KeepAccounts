package top.e404.keepaccounts.ui.component.record

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.ui.component.TagDisplayList
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun BalanceRecord(
    record: BalanceRecord,
    onClick: () -> Unit
) {
    val tagList by App.db.recordTag
        .flowByRecord(record.id)
        .collectAsStateWithLifecycle(initialValue = listOf())
    Surface(
        Modifier
            .padding(10.dp)
            .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(15.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable(onClick = onClick)
        ) {
            var time by remember { mutableStateOf("") }
            Row {
                Text(
                    when (record.type) {
                        RecordType.OUTCOME -> "支出"
                        RecordType.INCOME -> "收入"
                    },
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp, 0.dp),
                    color = when (record.type) {
                        RecordType.OUTCOME -> Color.Red
                        RecordType.INCOME -> Color.Green
                    }
                )
                Text("￥ ${record.value}", Modifier.align(Alignment.CenterVertically))
                if (record.desc.isNotEmpty()) Text(
                    "  (${record.desc})",
                    Modifier.align(Alignment.CenterVertically)
                )
            }
            Text(time, Modifier.padding(5.dp))
            TagDisplayList(tagList)
            LaunchedEffect(Unit) {
                while (true) {
                    val (display, delay) = formatTime(record.time)
                    time = display
                    delay(delay)
                }
            }
        }
    }
}

internal val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M月d日")

/**
 * 格式化时间
 *
 * @return 格式化后的时间和下次刷新的时间间隔, 单位ms
 */
private fun formatTime(time: Long): Pair<String, Long> {
    val now = System.currentTimeMillis()
    val diff = now - time
    return when {
        diff < 60 * 1000 -> "${(diff / 1000)}秒前" to 1000
        diff < 60 * 60 * 1000 -> "${(diff / (60 * 1000))}分钟前" to 60 * 1000
        diff < 24 * 60 * 60 * 1000 -> "${(diff / (60 * 60 * 1000))}小时前" to 60 * 60 * 1000
        diff < 7 * 24 * 60 * 60 * 1000 -> "${(diff / (24 * 60 * 60 * 1000))}天前" to 24 * 60 * 60 * 1000
        else -> {
            displayFormatter.format(
                LocalDateTime.ofEpochSecond(
                    time,
                    0,
                    ZoneOffset.of("+8")
                )
            ) to 24 * 60 * 60 * 1000
        }
    }
}