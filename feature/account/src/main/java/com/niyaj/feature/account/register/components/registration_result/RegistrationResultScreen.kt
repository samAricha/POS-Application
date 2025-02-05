package com.niyaj.feature.account.register.components.registration_result

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.core.ui.R.drawable
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.TextGray
import com.niyaj.feature.account.R
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedButtonFW
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun RegistrationResultScreen(
    navController: NavController,
    result: RegistrationResult,
    message: String,
) {
    Crossfade(
        targetState = result,
        label = "Submit Result State"
    ) {
        when (it) {
            RegistrationResult.Failure -> {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Scaffold(
                        content = { innerPadding ->
                            RegistrationResult(
                                title = stringResource(R.string.on_failure_register_title),
                                subtitle = stringResource(R.string.on_failure_register_subtitle),
                                description = stringResource(R.string.on_failure_register_desc),
                                image = drawable.emptystatetwo,
                                modifier = Modifier.padding(innerPadding)
                            )
                        },
                        bottomBar = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = 7.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceMedium),
                                    verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                                ) {
                                    NoteCard(text = message)

                                    StandardOutlinedButtonFW(
                                        text = stringResource(id = R.string.go_back),
                                        icon = Icons.AutoMirrored.Filled.NavigateBefore,
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    )
                }
            }

            RegistrationResult.Success -> {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = { innerPadding ->
                        RegistrationResult(
                            title = stringResource(R.string.on_success_register_title),
                            subtitle = stringResource(R.string.on_success_register_subtitle),
                            description = stringResource(R.string.on_success_register_desc),
                            image = drawable.emptystate,
                            modifier = Modifier.padding(innerPadding)
                        )
                    },
                    bottomBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 7.dp,
                        ) {
                            StandardButtonFW(
                                text = stringResource(id = R.string.done),
                                onClick = {
                                    navController.navigate(Screens.HOME_SCREEN) {
                                        popUpTo(navController.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                },
                                icon = Icons.Default.Done,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceMedium)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RegistrationResult(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    description: String,
    @DrawableRes
    image: Int,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
    ) {
        item {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(400.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.h6,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = description,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = TextGray,
            )
        }
    }
}


enum class RegistrationResult {
    Success,
    Failure,
}
