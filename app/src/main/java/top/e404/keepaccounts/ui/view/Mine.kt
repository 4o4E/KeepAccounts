package top.e404.keepaccounts.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import top.e404.keepaccounts.App
import top.e404.keepaccounts.R
import top.e404.keepaccounts.activity.ImportActivity
import top.e404.keepaccounts.data.entity.Export
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Mine(context: Context = LocalContext.current) {
    var showAbout by remember { mutableStateOf(false) }
    if (showAbout) AlertDialog(onDismissRequest = {
        showAbout = false
    }, text = {
        Text(text = "made by 404E and ❤")
    }, confirmButton = {
        ClickableText(
            text = AnnotatedString("确定"),
            onClick = { showAbout = false },
            style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
    })

    var exportFile by remember { mutableStateOf<String?>(null) }
    if (exportFile != null) AlertDialog(onDismissRequest = {
        exportFile = null
    }, title = {
        Text(text = "导出完成")
    }, text = {
        Text(text = "文件位于: 下载/${exportFile}")
    }, confirmButton = {
        ClickableText(
            text = AnnotatedString("确定"),
            onClick = { exportFile = null },
            style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
    })

    var importConfirm by remember { mutableStateOf(false) }
    if (importConfirm) AlertDialog(onDismissRequest = {
        importConfirm = true
    }, title = {
        Text(text = "导入确认")
    }, text = {
        Text(text = "导入数据后会覆盖本地现有数据, 确定继续?")
    }, confirmButton = {
        ClickableText(
            text = AnnotatedString("确定"),
            onClick = {
                importConfirm = false
                context.startActivity(Intent(context, ImportActivity::class.java))
            },
            style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
    }, dismissButton = {
        ClickableText(
            text = AnnotatedString("取消"),
            onClick = { importConfirm = false },
            style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
    })


    // var showExport by remember { mutableStateOf(false) }
    // if (showExport) FileSelector

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "app图标",
            modifier = Modifier.size(250.dp)
        )
        Column {
            val spanStyle = SpanStyle(
                fontSize = 1.3.em,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            ClickableText(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                text = AnnotatedString("关于", spanStyle),
                onClick = { showAbout = true },
            )
            HorizontalDivider()
            ClickableText(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                text = AnnotatedString("导出", spanStyle),
                onClick = {
                    val now = LocalDateTime.now().format(formatter)
                    val fileName = "keepaccounts_export_$now.json"
                    App.launch(Dispatchers.IO) {
                        val export = Export(
                            App.db.record.list(),
                            App.db.tag.list(),
                            App.db.recordTag.list(),
                        )
                        Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .resolve(fileName)
                            .writeText(Json.encodeToString(Export.serializer(), export))
                        withContext(Dispatchers.Main) {
                            exportFile = fileName
                        }
                    }
                },
            )
            HorizontalDivider()
            ClickableText(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                text = AnnotatedString("导入", spanStyle),
                onClick = { importConfirm = true },
            )
            // HorizontalDivider()
            // ClickableText(
            //     modifier = Modifier.padding(10.dp),
            //     text = AnnotatedString("设置", spanStyle),
            //     onClick = { controller.navigate(Router.Config) },
            // )
            // HorizontalDivider()
        }
    }
}

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")