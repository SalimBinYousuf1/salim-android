package com.salim.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.salim.android.service.SalimForegroundService
import com.salim.android.ui.screens.*
import com.salim.android.ui.theme.SalimTheme
import com.salim.android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Connection : Screen("connection", "Connection", Icons.Filled.Wifi)
    object Chats : Screen("chats", "Chats", Icons.Filled.Chat)
    object Status : Screen("status", "Status", Icons.Filled.Circle)
    object History : Screen("history", "History", Icons.Filled.History)
    object Admin : Screen("admin", "Admin", Icons.Filled.Settings)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Permission granted or denied — either way we start the service.
            // The notification will show only if granted; the service still runs either way.
            startSalimService()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionAndStartService()
        lifecycleScope.launch {
            vm.toast.collect { Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show() }
        }
        setContent { SalimTheme { SalimNavigation(vm) } }
    }

    private fun requestNotificationPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission for notifications
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startSalimService()
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startSalimService()
        }
    }

    private fun startSalimService() {
        try {
            startForegroundService(Intent(this, SalimForegroundService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun SalimNavigation(vm: MainViewModel) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Connection, Screen.Chats, Screen.Status, Screen.History, Screen.Admin)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(bottomBar = {
        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, screen.label) },
                    label = { Text(screen.label) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Screen.Connection.route, Modifier.padding(padding)) {
            composable(Screen.Connection.route) { ConnectionScreen(vm) }
            composable(Screen.Chats.route) { ChatsScreen(vm) }
            composable(Screen.Status.route) { StatusScreen(vm) }
            composable(Screen.History.route) { HistoryScreen(vm) }
            composable(Screen.Admin.route) { AdminScreen(vm) }
        }
    }
}


@Composable
fun SalimNavigation(vm: MainViewModel) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Connection, Screen.Chats, Screen.Status, Screen.History, Screen.Admin)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(bottomBar = {
        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, screen.label) },
                    label = { Text(screen.label) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Screen.Connection.route, Modifier.padding(padding)) {
            composable(Screen.Connection.route) { ConnectionScreen(vm) }
            composable(Screen.Chats.route) { ChatsScreen(vm) }
            composable(Screen.Status.route) { StatusScreen(vm) }
            composable(Screen.History.route) { HistoryScreen(vm) }
            composable(Screen.Admin.route) { AdminScreen(vm) }
        }
    }
}
