package com.salim.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.android.ui.theme.WhatsAppGreen
import com.salim.android.viewmodel.MainViewModel

@Composable
fun StatusScreen(vm: MainViewModel) {
    val isLoading by vm.isLoading.collectAsState()
    val generatedStatus by vm.generatedStatus.collectAsState()
    var manualText by remember { mutableStateOf("") }
    var topicText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("WhatsApp Status", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
        Spacer(Modifier.height(20.dp))

        // Manual post card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Post Status", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = manualText, onValueChange = { manualText = it },
                    label = { Text("Status text") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { if (manualText.isNotBlank()) { vm.postStatus(manualText); manualText = "" } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = manualText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) { Text("Post Status") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // AI generate card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("🤖 AI Generate Status", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("Let AI write a creative status for you", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = topicText, onValueChange = { topicText = it },
                    label = { Text("Topic (optional)") },
                    placeholder = { Text("e.g. feeling happy, Monday vibes...") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { vm.generateStatus(topicText) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("✨ Generate")
                }

                if (generatedStatus != null) {
                    Spacer(Modifier.height(12.dp))
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Generated:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(4.dp))
                            Text(generatedStatus!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { manualText = generatedStatus!! }, modifier = Modifier.weight(1f)) { Text("Use This") }
                        Button(
                            onClick = { vm.postStatus(generatedStatus!!); },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                        ) { Text("Post Now") }
                    }
                }
            }
        }
    }
}
