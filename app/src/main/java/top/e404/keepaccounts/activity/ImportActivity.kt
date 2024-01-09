package top.e404.keepaccounts.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.entity.Export
import java.io.BufferedReader

class ImportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.w("keepaccounts", result.toString())
            Log.w("keepaccounts", result.resultCode.toString())
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri == null) {
                    startActivity(Intent(this, App::class.java))
                    Toast.makeText(applicationContext, "取消导入", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                App.launch(Dispatchers.IO) {
                    val contentResolver = contentResolver

                    @SuppressLint("Recycle")
                    val stream = contentResolver.openInputStream(uri)
                    if (stream == null) {
                        startActivity(Intent(this@ImportActivity, App::class.java))
                        Toast.makeText(applicationContext, "文件不存在", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val (record, tag, recordTag) = stream.bufferedReader()
                        .use(BufferedReader::readText)
                        .let {
                            try {
                                Json.decodeFromString(Export.serializer(), it)
                            } catch (t: Throwable) {
                                startActivity(Intent(this@ImportActivity, App::class.java))
                                Toast.makeText(
                                    applicationContext,
                                    "文件内容错误, 取消导入",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }
                        }
                    App.db.record.importData(record)
                    App.db.tag.importData(tag)
                    App.db.recordTag.importData(recordTag)
                    println("导入完成")
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@ImportActivity, App::class.java))
                        Toast.makeText(applicationContext, "导入完成", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        })
    }
}