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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.OutlineSoft
import fi.antero.satumaa.ui.theme.StorybookPaper

/**
 * Sovelluksen yläpalkki (TopAppBar).
 *
 * Sisältää otsikon, takaisin-painikkeen (valinnainen) ja toimintovalikon (valinnainen).
 *
 * @param overrideTitle Kustomoitu otsikko. Jos null, käytetään oletusta "Satumaa".
 * @param showBack Näytetäänkö takaisin-nuoli.
 * @param onBack Callback takaisin-painikkeelle.
 * @param onOpenProfile Callback profiilin avaamiseen valikosta.
 * @param onOpenLibrary Callback kirjaston avaamiseen valikosta.
 * @param libraryLabel Teksti kirjaston avaamiselle (esim. "Omat sadut" tai "Omat kirjeet").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    overrideTitle: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onOpenProfile: (() -> Unit)? = null,
    onOpenLibrary: (() -> Unit)? = null,
    libraryLabel: String = stringResource(R.string.topbar_library_default)
) {
    // Tila pudotusvalikon näkyvyydelle
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = overrideTitle ?: stringResource(R.string.topbar_default_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                color = StorybookPaper
            )
        },
        navigationIcon = {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.topbar_back_cd),
                        tint = StorybookPaper
                    )
                }
            }
        },
        actions = {
            // Näytetään valikkoikoni vain, jos jompikumpi toiminto on määritelty
            if (onOpenLibrary != null || onOpenProfile != null) {
                IconButton(onClick = { setExpanded(true) }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.topbar_menu_cd),
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
                                    text = libraryLabel,
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
                                    text = stringResource(R.string.topbar_profile_label),
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