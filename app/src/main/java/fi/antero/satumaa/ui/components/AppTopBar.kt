package fi.antero.satumaa.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.OutlineSoft
import fi.antero.satumaa.ui.theme.StorybookPaper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    overrideTitle: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onOpenProfile: (() -> Unit)? = null,
    onOpenLibrary: (() -> Unit)? = null,
    libraryLabel: String = "Omat sadut"
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = overrideTitle ?: "Satumaa",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                color = StorybookPaper
            )
        },
        navigationIcon = {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Takaisin",
                        tint = StorybookPaper
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { setExpanded(true) }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Valikko",
                    tint = StorybookPaper
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                containerColor = StorybookPaper,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OutlineSoft),
                shadowElevation = 4.dp,
                modifier = Modifier.padding(end = 8.dp)
            ) {

                if (onOpenLibrary != null) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = libraryLabel, // Käytetään parametria tässä
                                style = MaterialTheme.typography.bodyLarge,
                                color = Ink
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = Forest
                            )
                        },
                        onClick = {
                            setExpanded(false)
                            onOpenLibrary()
                        }
                    )
                }

                if (onOpenProfile != null) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Profiili",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Ink
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Forest
                            )
                        },
                        onClick = {
                            setExpanded(false)
                            onOpenProfile()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = StorybookPaper,
            navigationIconContentColor = StorybookPaper,
            actionIconContentColor = StorybookPaper
        )
    )
}