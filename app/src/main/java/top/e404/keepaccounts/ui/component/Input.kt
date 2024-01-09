package top.e404.keepaccounts.ui.component

import android.annotation.SuppressLint
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun Input(
    label: String,
    value: String,
    readOnly: Boolean = false,
    focusRequester: FocusRequester? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Text(label) },
        value = value,
        readOnly = readOnly,
        onValueChange = onChange,
        modifier = modifier
            .also { if (focusRequester != null) it.focusRequester(focusRequester) },
    )
}