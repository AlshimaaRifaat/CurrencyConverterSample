package com.example.currencyconvertersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertersample.view_model.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterScreen()
        }
    }
}

@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel = hiltViewModel()) {
    val currencySymbols by viewModel.currencySymbols
    val errorMessage by viewModel.errorMessage

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorMessage(errorMessage)
        if (currencySymbols.isNotEmpty()) {
            CurrencySelectionDropdowns(currencySymbols, viewModel)
        } else {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorMessage(errorMessage: String?) {
    errorMessage?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.error
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CurrencySelectionDropdowns(
    currencySymbols: Map<String, String>,
    viewModel: CurrencyViewModel
) {
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }
    var fromCurrency by remember { mutableStateOf("") }
    var toCurrency by remember { mutableStateOf("") }
    val amount by viewModel.amount
    val convertedAmount by viewModel.convertedAmount

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
               viewModel.onAmountChanged(newAmount,fromCurrency,toCurrency)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))
        SwapButton()
        Spacer(modifier = Modifier.width(10.dp))
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
               viewModel.onConvertedAmountChanged(newConvertedAmount,fromCurrency,toCurrency)
            }
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
fun SwapButton() {
    Button(onClick = { /* Implement Swap Logic */ }) {
        Text("Swap")
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
        modifier = Modifier.width(200.dp)
    )
}
