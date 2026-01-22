package fi.antero.satumaa.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person // Lisätty profiili-ikoni
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
    onOpenLibrary: (() -> Unit)? = null
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
            // Valikkonappi
            IconButton(onClick = { setExpanded(true) }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Valikko",
                    tint = StorybookPaper
                )
            }

            // Tyylitelty pudotusvalikko
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                // 1. Lämmin paperin väri taustaksi
                containerColor = StorybookPaper,
                // 2. Pyöristetyt kulmat (pehmeämpi ilme)
                shape = RoundedCornerShape(16.dp),
                // 3. Hienovarainen reunus
                border = BorderStroke(1.dp, OutlineSoft),
                // Hieman varjoa erottamaan taustasta
                shadowElevation = 4.dp,
                modifier = Modifier.padding(end = 8.dp) // Pieni väli reunaan
            ) {

                if (onOpenLibrary != null) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Omat sadut",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Ink // Tumma teksti
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = Forest // Vihreä teemaväri
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
                            // Lisätty ikoni myös profiilille symmetrian vuoksi
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