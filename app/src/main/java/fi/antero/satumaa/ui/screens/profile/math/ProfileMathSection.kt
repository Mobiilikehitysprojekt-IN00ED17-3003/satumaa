package fi.antero.satumaa.ui.screens.profile.math

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.screens.profile.math.components.*
import fi.antero.satumaa.ui.viewmodel.stats.StatsViewModel

@Composable
fun ProfileMathSection(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeRange by viewModel.timeRange.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Otsikko varjolla (Valkoinen teksti, musta varjo takaa näkyvyyden kaikilla taustoilla)
        Text(
            text = stringResource(R.string.math_title_main),
            style = MaterialTheme.typography.headlineMedium.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            color = Color(0xFFFFF3E6), // StorybookPaper
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (uiState.isLoading) {
            Text(
                stringResource(R.string.math_status_loading),
                color = Color(0xFFFFF3E6),
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (uiState.totalStories == 0) {
            Text(
                stringResource(R.string.math_status_empty),
                color = Color(0xFFFFF3E6),
                textAlign = TextAlign.Center
            )
        } else {
            // 1. Aktiivisuus
            MathCard(
                title = stringResource(R.string.math_card_activity_title),
                description = stringResource(R.string.math_card_activity_desc)
            ) {
                TimeRangeSelector(
                    currentRange = timeRange,
                    onRangeSelected = { viewModel.setTimeRange(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeeklyActivityChart(uiState = uiState)
            }

            // 2. Mieltymykset
            MathCard(
                title = stringResource(R.string.math_card_keywords_title),
                description = stringResource(R.string.math_card_keywords_desc)
            ) {
                KeywordsPieChart(uiState = uiState)
            }

            // 3. Kehitys
            MathCard(
                title = stringResource(R.string.math_card_trend_title),
                description = stringResource(R.string.math_card_trend_desc)
            ) {
                TrendCombinedChart(uiState = uiState)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.math_trend_legend),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. Seikkailumittari
            MathCard(
                title = stringResource(R.string.math_card_adventure_title),
                description = stringResource(R.string.math_card_adventure_desc)
            ) {
                Text(
                    text = stringResource(R.string.math_adventure_tooltip),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF2E6B5B),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.math_adventure_info_title),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1F)
                        )
                        Text(
                            text = stringResource(R.string.math_adventure_info_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2A2A31),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.math_adventure_axis_legend),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    AdventureScatterChart(uiState = uiState)
                }
            }
        }
    }
}

@Composable
fun MathCard(
    title: String,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E6).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1B1F),
                textAlign = TextAlign.Center
            )

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}