package com.salim.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.android.data.model.Config
import com.salim.android.ui.theme.WhatsAppGreen
import com.salim.android.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(vm: MainViewModel) {
    val config by vm.config.collectAsState()
    val knowledge by vm.knowledge.collectAsState()

    LaunchedEffect(Unit) { vm.loadConfig(); vm.loadKnowledge() }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Admin Panel", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
        Spacer(Modifier.height(16.dp))
        if (config != null) ConfigPanel(config!!, vm)
        Spacer(Modifier.height(16.dp))
        KnowledgePanel(knowledge, vm)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfigPanel(config: Config, vm: MainViewModel) {
    var agentName by remember(config) { mutableStateOf(config.agent_name) }
    var customInstructions by remember(config) { mutableStateOf(config.custom_instructions) }
    var autoReply by remember(config) { mutableStateOf(config.auto_reply == "true") }
    var groupReactions by remember(config) { mutableStateOf(config.group_reactions == "true") }
    var temperature by remember(config) { mutableStateOf(config.temperature.toFloatOrNull() ?: 0.8f) }
    var maxTokens by remember(config) { mutableStateOf(config.max_tokens.toFloatOrNull() ?: 1024f) }
    var selectedTone by remember(config) { mutableStateOf(config.personality_tone) }
    var selectedHumor by remember(config) { mutableStateOf(config.humor_level) }

    val tones = listOf("friendly", "professional", "casual", "formal", "witty")
    val humors = listOf("none", "low", "medium", "high")

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Agent Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = agentName, onValueChange = { agentName = it },
                label = { Text("Agent Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            Text("Personality Tone", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tones.forEach { tone ->
                    FilterChip(
                        selected = selectedTone == tone,
                        onClick = { selectedTone = tone },
                        label = { Text(tone.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            Text("Humor Level", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                humors.forEach { h ->
                    FilterChip(
                        selected = selectedHumor == h,
                        onClick = { selectedHumor = h },
                        label = { Text(h.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = customInstructions, onValueChange = { customInstructions = it },
                label = { Text("Custom Instructions") },
                modifier = Modifier.fillMaxWidth().height(80.dp), maxLines = 3
            )
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Auto Reply")
                Switch(
                    checked = autoReply, onCheckedChange = { autoReply = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WhatsAppGreen)
                )
            }
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Group Reactions")
                Switch(
                    checked = groupReactions, onCheckedChange = { groupReactions = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WhatsAppGreen)
                )
            }
            Spacer(Modifier.height(12.dp))

            Text("Temperature: ${"%.1f".format(temperature)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Slider(
                value = temperature, onValueChange = { temperature = it },
                valueRange = 0f..1f, steps = 9,
                colors = SliderDefaults.colors(thumbColor = WhatsAppGreen, activeTrackColor = WhatsAppGreen)
            )
            Spacer(Modifier.height(4.dp))
            Text("Max Tokens: ${maxTokens.toInt()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Slider(
                value = maxTokens, onValueChange = { maxTokens = it },
                valueRange = 256f..4096f, steps = 14,
                colors = SliderDefaults.colors(thumbColor = WhatsAppGreen, activeTrackColor = WhatsAppGreen)
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    vm.saveConfig(mapOf(
                        "agent_name" to agentName,
                        "personality_tone" to selectedTone,
                        "humor_level" to selectedHumor,
                        "custom_instructions" to customInstructions,
                        "auto_reply" to autoReply.toString(),
                        "group_reactions" to groupReactions.toString(),
                        "temperature" to "%.1f".format(temperature),
                        "max_tokens" to maxTokens.toInt().toString()
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
            ) { Text("Save Configuration") }
        }
    }
}

@Composable
fun KnowledgePanel(
    knowledge: List<com.salim.android.data.model.KnowledgeItem>,
    vm: MainViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("general") }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Knowledge Base", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, "Add", tint = WhatsAppGreen)
                }
            }
            if (knowledge.isEmpty()) {
                Text(
                    "No knowledge items yet. Add some to help Salim respond better!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                knowledge.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(item.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            Text("[${item.category}]", style = MaterialTheme.typography.labelSmall, color = WhatsAppGreen)
                        }
                        IconButton(onClick = { vm.deleteKnowledge(item.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Divider()
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Knowledge") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = newContent, onValueChange = { newContent = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth().height(80.dp), maxLines = 3)
                    OutlinedTextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            vm.addKnowledge(newTitle, newContent, newCategory)
                            newTitle = ""; newContent = ""; newCategory = "general"
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
