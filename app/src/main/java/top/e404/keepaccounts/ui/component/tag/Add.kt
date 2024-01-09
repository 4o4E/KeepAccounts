package top.e404.keepaccounts.ui.component.tag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.ui.component.ColorPicker
import top.e404.keepaccounts.ui.component.Input
import top.e404.keepaccounts.ui.component.colors
import top.e404.keepaccounts.util.Update

@Composable
fun TagAdd(done: () -> Unit) {
    var tag by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(colors.random()) }
    var desc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 30.dp, 10.dp, 30.dp)
            .imePadding()
    ) {
        val focusRequester = remember { FocusRequester() }
        Input(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .focusRequester(focusRequester)
                .padding(5.dp),
            focusRequester = focusRequester,
            label = "标签",
            value = tag
        ) { tag = it }
        Input(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            label = "备注",
            value = desc,
        ) { desc = it }
        ColorPicker(
            color,
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp, 5.dp)
        ) { color = it }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                App.launch {
                    withContext(Dispatchers.IO) {
                        App.db.tag.insert(Tag(0, tag, desc, color.toArgb()))
                    }
                    done()
                    Update.record.longValue = System.currentTimeMillis()
                }
            }
        ) {
            Text(text = "保存")
        }
        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }
}