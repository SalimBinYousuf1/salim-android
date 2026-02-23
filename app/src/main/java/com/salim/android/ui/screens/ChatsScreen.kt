package com.salim.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.android.data.model.Chat
import com.salim.android.data.model.Message
import com.salim.android.ui.theme.BubbleIn
import com.salim.android.ui.theme.BubbleOut
import com.salim.android.ui.theme.WhatsAppGreen
import com.salim.android.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(vm: MainViewModel) {
    val chats by vm.chats.collectAsState()
    val messages by vm.messages.collectAsState()
    var selectedChat by remember { mutableStateOf<Chat?>(null) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.refreshChats() }

    if (selectedChat == null) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Chats") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppGreen, titleContentColor = Color.White)
            )
            if (chats.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💬", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No chats yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Connect WhatsApp to see conversations", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn {
                    items(chats, key = { it.jid }) { chat ->
                        ChatListItem(chat) { selectedChat = chat; vm.loadMessages(chat.jid) }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    } else {
        val chat = selectedChat!!
        val listState = rememberLazyListState()
        LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }

        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(CircleShape).padding(2.dp), contentAlignment = Alignment.Center) {
                            Text(chat.name.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(chat.name, fontSize = 16.sp, color = Color.White)
                            Text(if (chat.is_group == 1) "Group" else "Private", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { selectedChat = null }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppGreen)
            )
            LazyColumn(Modifier.weight(1f).padding(horizontal = 8.dp), state = listState) {
                items(messages, key = { it.id }) { msg -> MessageBubble(msg) }
            }
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = messageText, onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f), placeholder = { Text("Type a message") },
                    maxLines = 4, shape = RoundedCornerShape(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { if (messageText.isNotBlank()) { vm.sendMessage(chat.jid, messageText); messageText = "" } },
                    containerColor = WhatsAppGreen, modifier = Modifier.size(48.dp)
                ) { Icon(Icons.Default.Send, "Send", tint = Color.White) }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: Chat, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).clip(CircleShape).padding(4.dp), contentAlignment = Alignment.Center) {
            Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = WhatsAppGreen) {
                Box(contentAlignment = Alignment.Center) {
                    Text(chat.name.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(chat.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (chat.last_timestamp > 0) {
                    Text(formatTime(chat.last_timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(chat.last_message, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                if (chat.unread_count > 0) {
                    Surface(shape = CircleShape, color = WhatsAppGreen, modifier = Modifier.size(20.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(chat.unread_count.toString(), color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: Message) {
    val isMe = msg.from_me == 1
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
        Surface(
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = if (isMe) 12.dp else 4.dp, bottomEnd = if (isMe) 4.dp else 12.dp),
            color = if (isMe) BubbleOut else BubbleIn,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(msg.text, color = Color(0xFF111B21))
                Text(formatTime(msg.timestamp), fontSize = 10.sp, color = Color(0xFF667781), modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

fun formatTime(ts: Long): String {
    if (ts <= 0) return ""
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(if (ts > 1_000_000_000_000L) ts else ts * 1000))
}
