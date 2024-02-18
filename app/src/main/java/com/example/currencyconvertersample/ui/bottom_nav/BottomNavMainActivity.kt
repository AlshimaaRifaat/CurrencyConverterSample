package com.example.currencyconvertersample.ui.bottom_nav

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertersample.utils.BASE
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class BottomNavMainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = {
                    BottomNavigation {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        BottomNavigationItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Popular Currencies") },
                            label = { Text(text = "Popular Currencies") },
                            selected = currentDestination?.route == "popularCurrenciesScreen",
                            onClick = {
                                navController.navigate("popularCurrenciesScreen") {
                                    launchSingleTop = true
                                }
                            }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Filled.Info, contentDescription = "Historical Data") },
                            label = { Text(text = "Historical Data") },
                            selected = currentDestination?.route == "historicalDataScreen",
                            onClick = {
                                navController.navigate("historicalDataScreen") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) {
                NavHost(navController, startDestination = "popularCurrenciesScreen") {
                    val fromCurrency = intent.getStringExtra(BASE) ?: ""
                    composable("popularCurrenciesScreen") {
                        LatestRatesScreen(fromCurrency)
                    }
                    composable("historicalDataScreen") {
                        HistoricalDataScreen(fromCurrency)
                    }
                }
            }
        }
    }



}
