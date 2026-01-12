// app/src/main/java/fi/antero/satumaa/ui/components/AppTopBar.kt
package fi.antero.satumaa.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onOpenProfile: (() -> Unit)? = null
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(title) },
        navigationIcon = {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Takaisin")
                }
            }
        },
        actions = {
            IconButton(onClick = { setExpanded(true) }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Valikko")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) }
            ) {
                if (onOpenProfile != null) {
                    DropdownMenuItem(
                        text = { Text("Profiili") },
                        onClick = {
                            setExpanded(false)
                            onOpenProfile()
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Ei toimintoja vielä") },
                        onClick = { setExpanded(false) }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
