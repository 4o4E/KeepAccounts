package top.e404.keepaccounts

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.data.database.AppDb
import top.e404.keepaccounts.ui.component.colors
import top.e404.keepaccounts.ui.component.record.AddRecord
import top.e404.keepaccounts.ui.component.record.RecordBrowser
import top.e404.keepaccounts.ui.component.tag.TagBrowser
import top.e404.keepaccounts.ui.view.Config
import top.e404.keepaccounts.ui.view.Mine
import top.e404.keepaccounts.ui.view.Statistic
import top.e404.keepaccounts.util.Update

private val appScope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
    Log.e("top.e404.keepaccounts", "Unhandled exception in coroutine", throwable)
})

class App : ComponentActivity() {
    companion object : CoroutineScope by appScope {
        lateinit var db: AppDb
            private set
        private var init = false

        private const val FIRST_LAUNCH = "first_launch"
    }

    private inline fun firstLaunchInit(block: () -> Unit) {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        if (!preferences.getBoolean(FIRST_LAUNCH, true)) return
        block()
        preferences
            .edit()
            .putBoolean(FIRST_LAUNCH, false)
            .apply()
    }

    private fun init(context: Context) {
        if (init) return
        init = true
        db = Room.databaseBuilder(
            context, AppDb::class.java, "keepaccounts"
        ).build()
        firstLaunchInit {
            App.launch(Dispatchers.IO) {
                db.tag.insert(
                    Tag(tag = "早饭", desc = "一顿早饭", color = colors[0].toArgb()),
                    Tag(tag = "午饭", desc = "一顿午饭", color = colors[1].toArgb()),
                    Tag(tag = "晚饭", desc = "一顿晚饭", color = colors[2].toArgb()),
                    Tag(tag = "购物", desc = "买了点东西", color = colors[3].toArgb()),
                    Tag(
                        tag = "买贵了",
                        desc = "价格比预期的高, 反正买了",
                        color = colors[5].toArgb()
                    ),
                    Tag(tag = "赚到了", desc = "买的时候省钱了, 芜湖", color = colors[6].toArgb()),
                    Tag(tag = "转账", desc = "给别人转账", color = colors[7].toArgb()),
                    Tag(tag = "交通", desc = "出行", color = colors[8].toArgb()),
                    Tag(tag = "网购", desc = "淘宝, 京东, 拼多多", color = colors[9].toArgb()),
                    Tag(tag = "白花了", desc = "买的东西没用上", color = colors[10].toArgb()),
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(applicationContext)

        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                Application()
            }
        }
    }
}

object Router {
    const val Record = "record"
    const val Statistics = "statistics"
    const val Tag = "tag"
    const val Mine = "mine"
    const val Config = "mine/config"
}

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Application() {
    val controller = rememberNavController()
    val snackbarHostState = remember(Update.tag) { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val bottomBarHeight = 70.dp
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BottomAppBarDefaults.bottomAppBarFabColor)
                    .height(bottomBarHeight),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                BottomRouterButton(
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    text = "账单"
                ) {
                    controller.navigate(Router.Record)
                }
                BottomRouterButton(
                    icon = Icons.AutoMirrored.Outlined.Label,
                    text = "标签"
                ) {
                    controller.navigate(Router.Tag)
                }
                BottomRouterButton(
                    icon = Icons.Default.BarChart,
                    text = "统计"
                ) {
                    controller.navigate(Router.Statistics)
                }
                BottomRouterButton(
                    icon = Icons.Default.Person,
                    text = "我的"
                ) {
                    controller.navigate(Router.Mine)
                }
            }
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomBarHeight),
                color = MaterialTheme.colorScheme.background
            ) {
                CompositionLocalProvider(
                    NavController provides controller
                ) {
                    NavHost(navController = controller, startDestination = Router.Record) {
                        composable(Router.Record) { RecordBrowser() }
                        composable(Router.Statistics) { Statistic() }
                        composable(Router.Tag) { TagBrowser() }
                        composable(Router.Mine) { Mine() }
                        composable(Router.Config) { Config() }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            var show by remember { mutableStateOf(false) }
            @OptIn(ExperimentalMaterial3Api::class)
            if (show) {
                val modalBottomSheetState = rememberModalBottomSheetState(true) { true }
                ModalBottomSheet(
                    sheetState = modalBottomSheetState,
                    modifier = Modifier.fillMaxWidth(),
                    onDismissRequest = {
                        scope.launch {
                            show = false
                        }
                    }
                ) {
                    AddRecord(snackbarHostState) { show = false }
                }
            }
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        show = true
                    }
                }
            ) {
                Text(text = "+", color = Color.White, fontSize = 26.sp)
            }
        }
    )
}

@Stable
@SuppressLint("CompositionLocalNaming")
val NavController: ProvidableCompositionLocal<NavHostController> = staticCompositionLocalOf {
    error("no resource provide")
}

@Composable
fun BottomRouterButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(20.dp, 5.dp)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageVector = icon,
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = text
        )
    }
}
