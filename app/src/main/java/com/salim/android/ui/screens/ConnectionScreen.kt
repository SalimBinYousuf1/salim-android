package com.salim.android.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.android.ui.theme.WhatsAppGreen
import com.salim.android.viewmodel.MainViewModel

@Composable
fun ConnectionScreen(vm: MainViewModel) {
    val status by vm.connectionStatus.collectAsState()
    val qrCode by vm.qrCode.collectAsState()
    val phone by vm.phone.collectAsState()
    val pairingCode by vm.pairingCode.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val serverUrl by vm.serverUrl.collectAsState()

    var phoneInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf(serverUrl) }
    var showUrlEdit by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { vm.refreshStatus() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text("Salim AI Agent", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
        Spacer(Modifier.height(8.dp))

        // Status indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(
                when (status) {
                    "connected" -> Color(0xFF25D366)
                    "connecting" -> Color(0xFFFFC107)
                    else -> Color(0xFFE53935)
                }
            ))
            Spacer(Modifier.width(8.dp))
            Text(
                text = when (status) {
                    "connected" -> "Connected${phone?.let { " • ${it.substringBefore(":")} " } ?: ""}"
                    "connecting" -> "Connecting..."
                    else -> "Disconnected"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        // Server URL card
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Server URL", fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { showUrlEdit = !showUrlEdit; urlInput = serverUrl }) {
                        Text(if (showUrlEdit) "Cancel" else "Edit")
                    }
                }
                if (showUrlEdit) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Backend URL") }
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.setServerUrl(urlInput.trimEnd('/')); showUrlEdit = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Save & Reconnect")
                    }
                } else {
                    Text(serverUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (status != "connected") {
            // Tabs: QR / Phone pairing
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("QR Code", Modifier.padding(12.dp)) }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Phone Pairing", Modifier.padding(12.dp)) }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    if (qrCode != null) {
                        val bmp = remember(qrCode) {
                            try {
                                val b64 = qrCode!!.substringAfter("base64,")
                                val bytes = Base64.decode(b64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                            } catch (_: Exception) { null }
                        }
                        if (bmp != null) {
                            Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                                Image(bitmap = bmp, contentDescription = "QR Code", modifier = Modifier.size(250.dp).padding(8.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Scan with WhatsApp", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            CircularProgressIndicator(color = WhatsAppGreen)
                            Text("Generating QR...", Modifier.padding(top = 8.dp))
                        }
                    } else {
                        CircularProgressIndicator(color = WhatsAppGreen)
                        Text("Waiting for QR code...", Modifier.padding(top = 8.dp))
                    }
                }
                1 -> {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number") },
                                placeholder = { Text("+1234567890") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { vm.requestPairing(phoneInput) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = phoneInput.isNotBlank() && !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                else Text("Get Pairing Code")
                            }
                            if (pairingCode != null) {
                                Spacer(Modifier.height(12.dp))
                                Text("Pairing Code:", fontWeight = FontWeight.SemiBold)
                                Text(pairingCode!!, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen, letterSpacing = 4.sp)
                                Text("Enter this in WhatsApp > Linked Devices", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("WhatsApp Connected!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                    phone?.let { Text(it.substringBefore(":"), color = Color(0xFF388E3C)) }
                    Spacer(Modifier.height(8.dp))
                    Text("Salim is active and responding to messages", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { vm.refreshStatus() }, modifier = Modifier.fillMaxWidth()) {
            Text("Refresh Status")
        }
    }
}
