package top.e404.keepaccounts.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.e404.keepaccounts.util.contrastTextColor

val colors = listOf(
    Color(0xFFE74C3C),
    Color(0xFFC0392B),
    Color(0xFFD35400),
    Color(0xFFE67E22),
    Color(0xFFF39C12),
    Color(0xFFF1C40F),
    Color(0xFF16A085),
    Color(0xFF1ABC9C),
    Color(0xFF2ECC71),
    Color(0xFF27AE60),
    Color(0xFF2980B9),
    Color(0xFF3498DB),
    Color(0xFF9B59B6),
    Color(0xFF8E44AD),
    Color(0xFF2C3E50),
    Color(0xFF34495E),
    Color(0xFF95A5A6),
    Color(0xFF7F8C8D),
    Color(0xFFBDC3C7),
    Color(0xFFECF0F1)
)

@Composable
fun ColorPicker(color: Color, modifier: Modifier = Modifier, onChange: (Color) -> Unit) {
    var show by remember { mutableStateOf(false) }
    if (show) LazyRow(modifier) {
        items(colors.size) { index ->
            val c = colors[index]
            Box(
                Modifier
                    .size(50.dp)
                    .padding(5.dp)
                    .background(c)
                    .clickable {
                        onChange(c)
                        show = false
                    }
            )
        }
    }
    else Button(
        { show = true },
        colors = ButtonDefaults.buttonColors(color, color.contrastTextColor()),
        modifier = modifier
    ) { Text(text = "切换颜色") }
}