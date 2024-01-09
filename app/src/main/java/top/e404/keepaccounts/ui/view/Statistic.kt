package top.e404.keepaccounts.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.e404.keepaccounts.App
import top.e404.keepaccounts.ui.component.record.RecordType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField

@Composable
fun Statistic() {
    val today by App.db.record.listByTime(
        LocalDateTime.now()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000L,
        System.currentTimeMillis()
    ).collectAsStateWithLifecycle(initialValue = listOf())
    val week by App.db.record.listByTime(
        LocalDateTime.now()
            .with(ChronoField.DAY_OF_WEEK, 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000L,
        System.currentTimeMillis()
    ).collectAsStateWithLifecycle(initialValue = listOf())
    val month by App.db.record.listByTime(
        LocalDateTime.now()
            .with(ChronoField.DAY_OF_MONTH, 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000L,
        System.currentTimeMillis()
    ).collectAsStateWithLifecycle(initialValue = listOf())
    val year by App.db.record.listByTime(
        LocalDateTime.now()
            .with(ChronoField.DAY_OF_MONTH, 1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000L,
        System.currentTimeMillis()
    ).collectAsStateWithLifecycle(initialValue = listOf())

    Column(
        Modifier
            .fillMaxWidth()
            .padding(40.dp)
    ) {
        val modifier = Modifier.padding(10.dp)
        val todayIncome = today.filter { it.type == RecordType.INCOME }.sumOf { it.value }
        val todayOutcome = today.filter { it.type == RecordType.OUTCOME }.sumOf { it.value }
        Text(
            "今日收支: $todayIncome / $todayOutcome, 总计: ${todayIncome - todayOutcome}",
            modifier
        )
        val weekIncome = week.filter { it.type == RecordType.INCOME }.sumOf { it.value }
        val weekOutcome = week.filter { it.type == RecordType.OUTCOME }.sumOf { it.value }
        Text("本周收支: $weekIncome / $weekOutcome, 总计: ${weekIncome - weekOutcome}", modifier)
        val monthIncome = month.filter { it.type == RecordType.INCOME }.sumOf { it.value }
        val monthOutcome = month.filter { it.type == RecordType.OUTCOME }.sumOf { it.value }
        Text(
            "本月收支: $monthIncome / $monthOutcome, 总计: ${monthIncome - monthOutcome}",
            modifier
        )
        val yearIncome = year.filter { it.type == RecordType.INCOME }.sumOf { it.value }
        val yearOutcome = year.filter { it.type == RecordType.OUTCOME }.sumOf { it.value }
        Text("今年收支: $yearIncome / $yearOutcome, 总计: ${yearIncome - yearOutcome}", modifier)
        Text(text = "按标签筛选(未完成)", modifier)
        Text(text = "自定义时间段筛选(未完成)", modifier)
        Text(text = "自定义时间段筛选(未完成)", modifier)
    }
}