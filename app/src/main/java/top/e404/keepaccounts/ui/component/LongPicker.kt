package top.e404.keepaccounts.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog

@Composable
fun LongPicker(
    title: String,
    label: String,
    rangeStart: Long = Long.MIN_VALUE,
    rangeEnd: Long = Long.MAX_VALUE,
    hide: () -> Unit,
    set: (Long) -> Unit
) {
    var textValue by remember { mutableStateOf(TextFieldValue("0")) }

    Dialog(onDismissRequest = hide) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,
        ) {
            var verify by remember { mutableStateOf<String?>(null) }
            Column(Modifier.padding(20.dp)) {
                Text(text = title, style = TextStyle(fontSize = 6.em))

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    textValue,
                    {
                        textValue = it
                        verify = when {
                            textValue.text.isBlank() -> "请输入整数"
                            textValue.text.toLongOrNull() == null -> "请输入有效整数"
                            else -> null
                        }
                    },
                    label = { Text(text = if (verify != null) "$label - $verify" else label) }
                )

                Row(
                    Modifier
                        .align(Alignment.End)
                        .padding(0.dp, 30.dp, 20.dp, 20.dp)) {
                    val style = TextStyle(MaterialTheme.colorScheme.onBackground)
                    ClickableText(
                        text = AnnotatedString("取消"),
                        onClick = { hide() },
                        style = style
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    ClickableText(
                        text = AnnotatedString("保存"),
                        onClick = {
                            if (textValue.text.isBlank()) {
                                verify = "请输入整数"
                                return@ClickableText
                            }
                            val value = textValue.text.toLongOrNull()
                            if (value == null) {
                                verify = "请输入有效整数"
                                return@ClickableText
                            }
                            set(value)
                            hide()
                        }, style = style
                    )
                }
            }
        }
    }
}