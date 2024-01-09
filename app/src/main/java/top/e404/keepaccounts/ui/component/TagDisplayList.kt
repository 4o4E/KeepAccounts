package top.e404.keepaccounts.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.util.contrastTextColor

/** 用于展示tag的LazyRow, 没有点击操作 */
@Composable
fun TagDisplayList(tagList: List<Tag>, modifier: Modifier = Modifier) {
    LazyRow(modifier) {
        items(tagList.size) { index ->
            val tag = tagList[index]
            val tagColor = Color(tag.color)
            FilterChip(
                modifier = Modifier.padding(5.dp, 0.dp),
                colors = FilterChipDefaults.filterChipColors(
                    tagColor,
                    selectedContainerColor = tagColor
                ),
                onClick = {},
                label = { Text(tag.tag, color = tagColor.contrastTextColor()) },
                selected = false
            )
        }
    }
}