package com.roavai.parent.ui.child

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roavai.parent.R
import com.roavai.parent.ui.components.ScreenScaffold
import com.roavai.parent.ui.navigation.NavRoutes
import com.roavai.parent.ui.theme.RoavaiTheme

@Composable
fun AddChildScreen(
    viewModel: AddChildViewModel,
    onNavigateToHome: () -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AddChildUiState.Success) {
            onNavigateToHome()
        }
    }

    AddChildScreenContent(
        isLoading = uiState is AddChildUiState.Loading,
        onAddChild = { name, grade, board, consent ->
            viewModel.addChild(name, grade, board, consent)
        },
        onOpenDemoControls = onOpenDemoControls,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChildScreenContent(
    isLoading: Boolean,
    onAddChild: (String, String, String, Boolean) -> Unit,
    onOpenDemoControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var board by remember { mutableStateOf("") }

    var expandedGradeDropdown by remember { mutableStateOf(false) }
    var expandedBoardDropdown by remember { mutableStateOf(false) }

    // Four consent switches off by default
    var voiceConsent by remember { mutableStateOf(false) }
    var expressionConsent by remember { mutableStateOf(false) }
    var reasoningConsent by remember { mutableStateOf(false) }
    var trainingConsent by remember { mutableStateOf(false) }

    val gradeOptions = listOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    val boardOptions = listOf("CBSE", "ICSE", "State Board", "IB", "Cambridge")

    val isContinueEnabled = name.isNotBlank() && grade.isNotBlank() && board.isNotBlank() && !isLoading

    ScreenScaffold(
        title = stringResource(id = R.string.add_child_title),
        onTitleLongClick = onOpenDemoControls,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_child_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Child Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(id = R.string.add_child_field_name)) },
                placeholder = { Text("e.g., Aarav D'Souza") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Grade Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGradeDropdown,
                onExpandedChange = { expandedGradeDropdown = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = grade,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.add_child_field_grade)) },
                    placeholder = { Text("Select grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGradeDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedGradeDropdown,
                    onDismissRequest = { expandedGradeDropdown = false }
                ) {
                    gradeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                grade = option
                                expandedGradeDropdown = false
                            }
                        )
                    }
                }
            }

            // Board Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedBoardDropdown,
                onExpandedChange = { expandedBoardDropdown = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = board,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.add_child_field_board)) },
                    placeholder = { Text("Select board") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBoardDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedBoardDropdown,
                    onDismissRequest = { expandedBoardDropdown = false }
                ) {
                    boardOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                board = option
                                expandedBoardDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Consents Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.add_child_consent_section_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.add_child_consent_section_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    ConsentSwitchItem(
                        title = stringResource(id = R.string.profile_consent_voice),
                        description = stringResource(id = R.string.add_child_consent_voice_desc),
                        checked = voiceConsent,
                        onCheckedChange = { voiceConsent = it }
                    )

                    ConsentSwitchItem(
                        title = stringResource(id = R.string.profile_consent_expression),
                        description = stringResource(id = R.string.add_child_consent_expression_desc),
                        checked = expressionConsent,
                        onCheckedChange = { expressionConsent = it }
                    )

                    ConsentSwitchItem(
                        title = stringResource(id = R.string.profile_consent_reasoning),
                        description = stringResource(id = R.string.add_child_consent_reasoning_desc),
                        checked = reasoningConsent,
                        onCheckedChange = { reasoningConsent = it }
                    )

                    ConsentSwitchItem(
                        title = stringResource(id = R.string.profile_consent_training),
                        description = stringResource(id = R.string.add_child_consent_training_desc),
                        checked = trainingConsent,
                        onCheckedChange = { trainingConsent = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val anyConsent = voiceConsent || expressionConsent || reasoningConsent || trainingConsent
                    onAddChild(name.trim(), grade, board, anyConsent)
                },
                enabled = isContinueEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(id = R.string.add_child_btn_continue))
                }
            }
        }
    }
}

@Composable
fun ConsentSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddChildScreenPreview() {
    RoavaiTheme {
        AddChildScreenContent(
            isLoading = false,
            onAddChild = { _, _, _, _ -> },
            onOpenDemoControls = {}
        )
    }
}
