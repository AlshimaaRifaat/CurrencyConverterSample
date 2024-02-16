package com.example.currencyconvertersample.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertersample.ui.bottomNav.BottomNavMainActivity
import com.example.currencyconvertersample.utils.Resource
import com.example.currencyconvertersample.view_model.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "currencyConverter") {
                    composable("currencyConverter") {
                        CurrencyConverterScreen(navController)
                    }
                    composable("bottomNavigation") {
                        // Your bottom navigation screen
                    }
                }

        }
    }
}

@Composable
fun CurrencyConverterScreen(navController: NavController, viewModel: CurrencyViewModel = hiltViewModel()) {
    val currencySymbolsResponse by viewModel.currencySymbolsResponse


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currencySymbolsResponse) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                val symbols = currencySymbolsResponse.data?.symbols ?: emptyMap()
                if (symbols.isNotEmpty()) {
                    CurrencySelectionDropdowns(symbols, viewModel, navController)
                } else {
                    // Handle empty symbols map
                }
            }
            is Resource.Error -> {
                Text(
                    text = "Error: ${(currencySymbolsResponse as Resource.Error).message}",
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}


@Composable
fun CurrencySelectionDropdowns(
    currencySymbols: Map<String, String>,
    viewModel: CurrencyViewModel,
    navController: NavController
) {
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }
    var fromCurrency by viewModel.fromCurrency
    var toCurrency by viewModel.toCurrency
    val amount by viewModel.amount
    val convertedAmount by viewModel.convertedAmount
    val context= LocalContext.current

    Column {
        Row {
            CurrencyDropdown(
                expanded = expandedFrom,
                onExpandChange = { expandedFrom = it },
                selectedCurrency = fromCurrency,
                onCurrencySelected = { fromCurrency = it },
                currencySymbols = currencySymbols,
                label = "From"
            )
            AmountInputField(amount, "Amount") {newAmount ->
               viewModel.onAmountChanged(newAmount)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        CommonButton("Swap") {
            viewModel.swapCurrencies(fromCurrency, toCurrency)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            CurrencyDropdown(
                expanded = expandedTo,
                onExpandChange = { expandedTo = it },
                selectedCurrency = toCurrency,
                onCurrencySelected = { toCurrency = it },
                currencySymbols = currencySymbols,
                label = "To"
            )
            AmountInputField(convertedAmount, "Converted Amount"){newConvertedAmount->
               viewModel.onConvertedAmountChanged(newConvertedAmount)
            }
        }
        CommonButton("Details"){
            // Start BottomNavMainActivity
            val intent = Intent(context, BottomNavMainActivity::class.java)
            context.startActivity(intent)
        }

    }
}

@Composable
fun CurrencyDropdown(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    currencySymbols: Map<String, String>,
    label: String
) {
    Column {
        Button(onClick = { onExpandChange(true) }) {
            Text(if (selectedCurrency.isEmpty()) label else selectedCurrency)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            currencySymbols.keys.forEach { currencyCode ->
                DropdownMenuItem(onClick = {
                    onCurrencySelected(currencyCode)
                    onExpandChange(false)
                }) {
                    Text(text = "$currencyCode - ${currencySymbols[currencyCode]}")
                }
            }
        }
    }
}

@Composable
fun CommonButton(text: String,onButtonClicked: ()-> Unit) {
    Button(onClick = { onButtonClicked() }) {
        Text(text)
    }
}

@Composable
fun AmountInputField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
           if (newValue.all { it.isDigit() || it == '.' }) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
