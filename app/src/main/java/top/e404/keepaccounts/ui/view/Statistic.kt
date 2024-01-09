package top.e404.keepaccounts.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.query.RecordQuery
import top.e404.keepaccounts.ui.component.DateTimePicker
import top.e404.keepaccounts.ui.component.LongPicker
import top.e404.keepaccounts.ui.component.TextPicker
import top.e404.keepaccounts.ui.component.record.BalanceRecord
import top.e404.keepaccounts.ui.component.record.EditRecord
import top.e404.keepaccounts.util.ViewModel
import top.e404.keepaccounts.util.contrastTextColor

@Composable
fun Statistic() {
    var query by remember { mutableStateOf(RecordQuery()) }

    // date time picker
    var pickerState by remember { mutableStateOf(PickerState.HIDE) }
    if (pickerState != PickerState.HIDE) DateTimePicker(
        timestamp = System.currentTimeMillis(),
        hide = { pickerState = PickerState.HIDE },
        set = {
            query =
                if (pickerState == PickerState.START) query.edit(timeStart = it)
                else query.edit(timeEnd = it)
            ViewModel.updateRecordFilterList(query)
        }
    )

    // value time picker
    var valueState by remember { mutableStateOf(PickerState.HIDE) }
    if (valueState != PickerState.HIDE) LongPicker(
        "输入数值",
        if (valueState == PickerState.START) "最小值" else "最大值",
        hide = { valueState = PickerState.HIDE }
    ) {
        query =
            if (valueState == PickerState.START) query.edit(valueStart = it)
            else query.edit(valueEnd = it)
        ViewModel.updateRecordFilterList(query)
    }

    var showDescPicker by remember { mutableStateOf(false) }
    if (showDescPicker) TextPicker(
        title = "输入备注",
        label = "备注",
        hide = { showDescPicker = false },
        set = {
            query = query.edit(desc = it)
            ViewModel.updateRecordFilterList(query)
        }
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        val modifier = Modifier.padding(5.dp)
        val selected =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer.contrastTextColor()
            )
        val normal = ButtonDefaults.buttonColors()
        LazyRow {
            item {
                Button(
                    onClick = {
                        if (query.timeStart == null) pickerState = PickerState.START
                        else {
                            query = query.edit(timeStart = null)
                            ViewModel.updateRecordFilterList(query)
                        }
                    },
                    modifier = modifier,
                    colors = if (query.timeStart != null) selected else normal
                ) { Text("开始时间") }
            }
            item {
                Button(
                    onClick = {
                        if (query.timeEnd == null) pickerState = PickerState.END
                        else {
                            query = query.edit(timeEnd = null)
                            ViewModel.updateRecordFilterList(query)
                        }
                    },
                    modifier = modifier,
                    colors = if (query.timeEnd != null) selected else normal
                ) { Text("结束时间") }
            }
            item {
                Button(
                    onClick = {
                        if (query.valueStart == null) valueState = PickerState.START
                        else {
                            query = query.edit(valueStart = null)
                            ViewModel.updateRecordFilterList(query)
                        }
                    },
                    modifier = modifier,
                    colors = if (query.valueStart != null) selected else normal
                ) { Text("金额最小值") }
            }
            item {
                Button(
                    onClick = {
                        if (query.valueEnd == null) valueState = PickerState.END
                        else {
                            query = query.edit(valueEnd = null)
                            ViewModel.updateRecordFilterList(query)
                        }
                    },
                    modifier = modifier,
                    colors = if (query.valueEnd != null) selected else normal
                ) { Text("金额最大值") }
            }
            item {
                Button(
                    onClick = {
                        if (query.desc == null) showDescPicker = true
                        else {
                            query = query.edit(desc = null)
                            ViewModel.updateRecordFilterList(query)
                        }
                    },
                    modifier = modifier,
                    colors = if (query.desc != null) selected else normal
                ) { Text("备注筛选") }
            }
        }

        val scope = rememberCoroutineScope()
        val list by remember { ViewModel.recordFilterList }
        var editing by remember { mutableStateOf<BalanceRecord?>(null) }

        @OptIn(ExperimentalMaterial3Api::class)
        if (editing != null) ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(true) { true },
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                scope.launch { editing = null }
            }
        ) {
            EditRecord(editing as BalanceRecord) { editing = null }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            LazyColumn(Modifier.align(Alignment.CenterHorizontally)) {
                items(list.size) { index ->
                    val record = list[index]
                    BalanceRecord(record = record) { editing = record }
                }
            }
        }
        LaunchedEffect(Unit) {
            ViewModel.updateRecordFilterList(query)
        }
    }
}

enum class PickerState {
    START, END, HIDE
}