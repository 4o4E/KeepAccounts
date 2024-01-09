package top.e404.keepaccounts.ui.component.tag

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.util.contrastTextColor

@Composable
fun TagBrowser() {
    val scope = rememberCoroutineScope()
    val list = App.db.tag.flow().collectAsStateWithLifecycle(initialValue = listOf())
    var editingTag by remember { mutableStateOf<Tag?>(null) }

    var addingTag by remember { mutableStateOf(false) }
    @OptIn(ExperimentalMaterial3Api::class)
    if (editingTag != null) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(true) { true },
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                scope.launch { editingTag = null }
            }
        ) {
            TagEdit(editingTag as Tag) { editingTag = null }
        }
    } else if (addingTag) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(true) { true },
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                scope.launch { addingTag = false }
            }
        ) {
            TagAdd { addingTag = false }
        }
    }

    Column {
        LazyColumn(
            Modifier
                .align(Alignment.Start)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            items(list.value.size) { index ->
                val tag = list.value[index]
                val color = Color(tag.color)
                Surface(
                    Modifier
                        .padding(5.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            RoundedCornerShape(15.dp)
                        )
                        .clickable { editingTag = tag }
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color,
                                contentColor = color.contrastTextColor()
                            ), onClick = { editingTag = tag }) { Text(text = tag.tag) }
                        Text(
                            tag.desc,
                            Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            item {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            RoundedCornerShape(15.dp)
                        )
                        .clickable { addingTag = true }
                ) {
                    Row {
                        Button(
                            { addingTag = true },
                            Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                        Text("添加Tag",
                            Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterVertically))
                    }
                }
            }
        }
    }
}