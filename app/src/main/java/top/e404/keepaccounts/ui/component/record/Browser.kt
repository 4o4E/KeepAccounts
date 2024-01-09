package top.e404.keepaccounts.ui.component.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.BalanceRecord

@Composable
fun RecordBrowser() {
    val scope = rememberCoroutineScope()
    val list by App.db.record.flow().collectAsStateWithLifecycle(initialValue = listOf())
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

    Column {
        LazyColumn(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
        ) {
            items(list.size) { index ->
                val record = list[index]
                BalanceRecord(record = record) { editing = record }
            }
        }
    }
}