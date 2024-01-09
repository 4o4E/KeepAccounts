package top.e404.keepaccounts.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.e404.keepaccounts.App
import top.e404.keepaccounts.data.dao.Tag
import top.e404.keepaccounts.util.contrastTextColor

/** 用于选择Tag的列表 */
@Composable
fun TagSelectList(
    selected: List<Tag> = listOf(),
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier,
    onChange: (List<Tag>) -> Unit
) {
    var selectList by remember { mutableStateOf(selected) }
    val tagList by App.db.recordTag.listByUsedDegree().collectAsState(initial = listOf())

    LazyRow(modifier) {
        items(tagList.size) { index ->
            val tag = tagList[index]
            val isSelected = tag in selectList
            val tagColor = Color(tag.color)
            FilterChip(
                modifier = Modifier.padding(5.dp),
                colors = FilterChipDefaults.filterChipColors(
                    tagColor,
                    selectedContainerColor = tagColor
                ),
                onClick = {
                    val new = if (isSelected) selectList - tag else selectList + tag
                    selectList = new
                    onChange(new)
                },
                label = { Text(tag.tag, color = tagColor.contrastTextColor()) },
                selected = isSelected,
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Done, null, Modifier.size(FilterChipDefaults.IconSize)) }
                } else null,
            )
        }
    }
}