package top.e404.keepaccounts.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(timestamp: Long, hide: () -> Unit, set: (Long) -> Unit) {
    var isDate by remember { mutableStateOf(true) }
    var time by remember { mutableLongStateOf(timestamp) }

    val now = OffsetDateTime.ofInstant(Instant.ofEpochSecond(time / 1000), ZoneId.systemDefault())
    val datePickerState = rememberDatePickerState(time)
    val timePickerState = rememberTimePickerState(now.hour, now.minute, is24Hour = true)
    Dialog(onDismissRequest = hide) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                TabRow(selectedTabIndex = if (isDate) 0 else 1) {
                    Tab(selected = isDate, onClick = { isDate = true }) {
                        Text("日期", Modifier.padding(10.dp))
                    }
                    Tab(selected = !isDate, onClick = { isDate = false }) {
                        Text("时间", Modifier.padding(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val modifier = Modifier
                    .height(500.dp)
                    .align(Alignment.CenterHorizontally)
                if (isDate) DatePicker(datePickerState, modifier)
                else TimePicker(timePickerState, modifier)

                Row(
                    Modifier
                        .align(Alignment.End)
                        .padding(15.dp)
                ) {
                    val style = TextStyle(MaterialTheme.colorScheme.onBackground)
                    ClickableText(
                        text = AnnotatedString("取消"),
                        onClick = { hide() },
                        modifier = Modifier.padding(15.dp),
                        style = style
                    )
                    ClickableText(
                        text = AnnotatedString("保存"),
                        onClick = {
                            time = LocalDateTime.of(
                                Instant.ofEpochSecond(datePickerState.selectedDateMillis!! / 1000)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate(),
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                            ).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                            set(time)
                            hide()
                        },
                        modifier = Modifier.padding(15.dp), style = style
                    )
                }
            }
        }
    }
}