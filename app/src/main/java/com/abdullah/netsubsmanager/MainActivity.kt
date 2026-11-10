package com.abdullah.netsubsmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.ui.screens.*
import com.abdullah.netsubsmanager.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = Repository(this)
        setContent {
            AppTheme {
                val nav = rememberNavController()
                Scaffold {
                    AppNavHost(nav, repo)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(nav: NavHostController, repo: Repository) {
    NavHost(navController = nav, startDestination = Routes.Dashboard) {
        composable(Routes.Dashboard) { DashboardScreen(nav, repo) }
        composable(Routes.Accounts) { AccountsScreen(nav, repo) }
        composable(Routes.Customers) { CustomersScreen(nav, repo) }
        composable(Routes.ImportExport) { ImportExportScreen(nav, repo) }
        composable(Routes.Payments) { PaymentsScreen(nav, repo) }
        composable("customerDetails/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: -1
            CustomerDetailsScreen(nav, repo, id)
        }
        composable("assign/{accountId}") { backStack ->
            val accountId = backStack.arguments?.getString("accountId")?.toLongOrNull() ?: -1
            AssignScreen(nav, repo, accountId)
        }
        composable(Routes.Settings) { SettingsScreen(nav, repo) }
    }
}

object Routes {
    const val Dashboard = "dashboard"
    const val Accounts = "accounts"
    const val Customers = "customers"
    const val ImportExport = "import"
    const val Payments = "payments"
    const val Settings = "settings"
}
