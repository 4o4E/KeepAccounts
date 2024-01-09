package top.e404.keepaccounts.ui.component.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.room.TypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.BalanceRecord
import top.e404.keepaccounts.data.dao.RecordTag
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.ui.component.DateTimePicker
import top.e404.keepaccounts.ui.component.TagSelectList
import top.e404.keepaccounts.util.primitive
import java.math.BigDecimal


private val numberRegex = Regex("\\d+((\\.)\\d{0,2})?")

@Composable
fun AddRecord(snackbarHostState: SnackbarHostState, done: CoroutineScope.() -> Unit) {
    var chosenTags by remember { mutableStateOf(listOf<Tag>()) }
    var value by remember { mutableStateOf(TextFieldValue()) }
    var desc by remember { mutableStateOf(TextFieldValue()) }
    var type by remember { mutableStateOf(RecordType.OUTCOME) }
    var time by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) DateTimePicker(System.currentTimeMillis(), { showPicker = false }) { time = it }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 30.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val keyboard = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        var valueError by remember { mutableStateOf<String?>(null) }
        val onDone: () -> Unit = onClick@{
            if (value.text.isEmpty()) {
                valueError = "请输入金额"
                return@onClick
            }
            App.launch(Dispatchers.IO) {
                val record = BalanceRecord(
                    type = type,
                    value = BigDecimal(value.text),
                    time = time,
                    desc = desc.text
                )
                val recordId = App.db.record.insertReturnId(record)
                App.db.recordTag.insert(chosenTags.map { RecordTag(recordId, it.id) })
                withContext(Dispatchers.IO) {
                    keyboard?.hide()
                    value = TextFieldValue()
                    desc = TextFieldValue()
                    chosenTags = listOf()
                    done()
                }
            }
        }
        Row {
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
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { showPicker = true }) { Text(text = "选择时间") }
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = {
                Text(text = buildString {
                    append("金额")
                    if (valueError != null) append(" - ").append(valueError)
                })
            },
            modifier = Modifier.focusRequester(focusRequester),
            value = value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = valueError != null,
            onValueChange = {
                value = it
                valueError = if (it.text.matches(numberRegex)) null else "无效金额"
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "备注") },
            modifier = Modifier,
            value = desc,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions { onDone() },
            onValueChange = { desc = it }
        )
        Spacer(modifier = Modifier.height(20.dp))
        TagSelectList { chosenTags = it }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onDone) { Text(text = "添加") }
        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }
}

@Serializable(RecordType.Serializer::class)
enum class RecordType(val code: Int, val display: String) {
    OUTCOME(0, "支出"),
    INCOME(1, "收入");

    companion object {
        fun getByCode(code: Int) = entries.first { it.code == code }
    }

    object Serializer : KSerializer<RecordType> {
        override val descriptor = primitive(PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder) = getByCode(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: RecordType) = encoder.encodeInt(value.code)
    }

    object Converter {
        @TypeConverter
        fun RecordType.convert() = code


        @TypeConverter
        fun Int.convert() = getByCode(this)
    }
}