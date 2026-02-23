package com.salim.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.android.data.model.AIInteraction
import com.salim.android.ui.theme.WhatsAppGreen
import com.salim.android.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(vm: MainViewModel) {
    val history by vm.history.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.loadHistory() }

    Column(Modifier.fillMaxSize()) {
        // Stats row
        if (history != null) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total", history!!.total.toString(), Modifier.weight(1f))
                StatCard("Today", history!!.today.toString(), Modifier.weight(1f))
            }
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; vm.loadHistory(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Search conversations...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.height(8.dp))

        val interactions = history?.interactions ?: emptyList()
        if (interactions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤖", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No AI interactions yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(interactions, key = { it.id }) { InteractionCard(it) }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WhatsAppGreen)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
            Text(label, style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun InteractionCard(interaction: AIInteraction) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(interaction.sender ?: interaction.jid.substringBefore("@"), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                Text(formatTimestamp(interaction.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                Text("👤 ${interaction.user_message}", Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                Text("🤖 ${interaction.ai_response}", Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Text("Model: ${interaction.model_used.substringAfterLast("/")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun formatTimestamp(ts: Long): String {
    if (ts <= 0) return ""
    val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    return sdf.format(Date(if (ts > 1_000_000_000_000L) ts else ts * 1000))
}
