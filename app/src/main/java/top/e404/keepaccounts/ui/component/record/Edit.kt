package top.e404.keepaccounts.ui.component.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.ui.component.DateTimePicker
import top.e404.keepaccounts.ui.component.Input
import top.e404.keepaccounts.ui.component.TagDisplayList
import top.e404.keepaccounts.ui.component.TagSelectList
import top.e404.keepaccounts.util.Update
import top.e404.keepaccounts.util.toLocalDateTime
import java.math.BigDecimal

@Composable
fun EditRecord(recordDo: BalanceRecord, done: () -> Unit) {
    var type by remember { mutableStateOf(recordDo.type) }
    var desc by remember { mutableStateOf(recordDo.desc) }
    var value by remember { mutableStateOf(recordDo.value.toString()) }
    var time by remember { mutableLongStateOf(recordDo.time) }
    var selectedTags by remember { mutableStateOf(listOf<Tag>()) }
    var isEdit by remember { mutableStateOf(false) }

    var showPicker by remember { mutableStateOf(false) }
    if (showPicker) DateTimePicker(time, { showPicker = false }) { time = it }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = "删除确认") },
            text = {
                Text(
                    "确定删除${
                        recordDo.time.toLocalDateTime().format(displayFormatter)
                    }的账单吗?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        App.launch(Dispatchers.IO) {
                            App.db.record.delete(recordDo)
                            withContext(Dispatchers.Main) {
                                showDeleteConfirm = false
                                done()
                            }
                        }
                    }
                ) { Text("确认") }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirm = false }) { Text("取消") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 30.dp)
            .imePadding()
    ) {
        val focusRequester = remember { FocusRequester() }
        Row(
            Modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            IconButton(onClick = { isEdit = !isEdit }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = {
                showDeleteConfirm = true
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            @OptIn(ExperimentalMaterial3Api::class)
            MultiChoiceSegmentedButtonRow {
                for ((index, currentType) in RecordType.entries.withIndex()) {
                    this@MultiChoiceSegmentedButtonRow.SegmentedButton(
                        modifier = Modifier,
                        checked = currentType == type,
                        onCheckedChange = { type = currentType },
                        shape = SegmentedButtonDefaults.itemShape(index, RecordType.entries.size)
                    ) {
                        Text(text = currentType.display)
                    }
                }
            }
            if (isEdit) {
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = { showPicker = true }) { Text(text = "选择时间") }
            }
        }
        Input(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .focusRequester(focusRequester)
                .padding(5.dp),
            readOnly = !isEdit,
            focusRequester = focusRequester,
            label = "金额",
            value = value,
            onChange = { value = it }
        )
        Input(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            readOnly = !isEdit,
            label = "备注",
            value = desc,
            onChange = { desc = it }
        )
        val modifier = Modifier.align(Alignment.CenterHorizontally)
        if (isEdit) TagSelectList(selectedTags, modifier) { selectedTags = it }
        else TagDisplayList(selectedTags, modifier)
        LaunchedEffect(Unit) {
            App.launch(Dispatchers.IO) {
                selectedTags = App.db.recordTag.listByRecord(recordDo.id)
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            onClick = {
                App.launch(Dispatchers.IO) {
                    if (isEdit) {
                        App.db.record.update(recordDo.also {
                            it.type = type
                            it.desc = desc
                            it.value = BigDecimal(value)
                            it.time = time
                        })
                        App.db.recordTag.updateByRecord(recordDo.id, selectedTags.map { it.id })
                    }
                    withContext(Dispatchers.Main) {
                        Update.record.longValue = System.currentTimeMillis()
                        done()
                    }
                }
            }
        ) {
            Text(if (isEdit) "保存" else "确定")
        }
        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }
}
