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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.ui.component.ColorPicker
import top.e404.keepaccounts.ui.component.Input
import top.e404.keepaccounts.util.ViewModel

@Composable
fun TagEdit(tagDo: Tag, done: () -> Unit) {
    var tag by remember { mutableStateOf(tagDo.tag) }
    var color by remember { mutableIntStateOf(tagDo.color) }
    var desc by remember { mutableStateOf(tagDo.desc) }
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
            value = tag,
            onChange = { tag = it }
        )
        Input(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            label = "备注",
            value = desc,
            onChange = { desc = it }
        )
        ColorPicker(
            Color(color),
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp, 5.dp)
        ) {
            color = it.toArgb()
        }
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                App.launch(Dispatchers.IO) {
                    App.db.tag.update(tagDo.also {
                        it.tag = tag
                        it.color = color
                        it.desc = desc
                    })
                    withContext(Dispatchers.Main) {
                        ViewModel.updateTagList()
                        done()
                    }
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