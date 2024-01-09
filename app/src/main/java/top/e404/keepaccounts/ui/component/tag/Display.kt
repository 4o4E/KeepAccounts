package top.e404.keepaccounts.ui.component.tag

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.util.contrastTextColor

@Composable
fun TagDisplay(tag: Tag, onClick: (Tag) -> Unit) {
    Surface(
        Modifier
            .padding(5.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.onPrimaryContainer,
                RoundedCornerShape(15.dp)
            )
            .clickable { onClick(tag) }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterVertically),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(tag.color),
                    contentColor = Color(tag.color).contrastTextColor()
                ), onClick = { onClick(tag) }) { Text(text = tag.tag) }
            Text(
                tag.desc,
                Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}