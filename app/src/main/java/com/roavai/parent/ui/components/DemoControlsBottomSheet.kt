package com.roavai.parent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.data.fake.DemoSettings
import com.roavai.parent.data.fake.DemoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoControlsBottomSheet(
    demoSettings: DemoSettings,
    onDismissRequest: () -> Unit
) {
    val currentState by demoSettings.state.collectAsStateWithLifecycle()
    val isRobotDisconnected by demoSettings.robotDisconnected.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(id = R.string.demo_controls_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val states = listOf(
                DemoState.NORMAL to R.string.demo_state_normal,
                DemoState.LOADING to R.string.demo_state_loading,
                DemoState.EMPTY to R.string.demo_state_empty,
                DemoState.ERROR to R.string.demo_state_error,
                DemoState.OFFLINE to R.string.demo_state_offline
            )

            states.forEach { (state, labelRes) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (currentState == state),
                            onClick = { demoSettings.setDemoState(state) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentState == state),
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = labelRes),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { demoSettings.setRobotDisconnected(!isRobotDisconnected) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.demo_robot_disconnected),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isRobotDisconnected,
                    onCheckedChange = { demoSettings.setRobotDisconnected(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.demo_close))
            }
        }
    }
}
