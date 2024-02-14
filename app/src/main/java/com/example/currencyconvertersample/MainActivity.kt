package com.example.currencyconvertersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.currencyconvertersample.view_model.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterApp()
        }
    }

    @Composable
    fun CurrencyConverterApp(viewModel: CurrencyViewModel = hiltViewModel()) {
        // Directly observe MutableState values
        val currencySymbols by viewModel.currencySymbols
        val errorMessage by viewModel.errorMessage

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.error
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (currencySymbols.isNotEmpty()) {
                CurrencySelectionDropdowns(currencySymbols = currencySymbols, viewModel = viewModel)
            } else {
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    fun CurrencySelectionDropdowns(
        currencySymbols: Map<String, String>,
        viewModel: CurrencyViewModel
    ) {
        // Remember expanded state for each dropdown
        var expandedFrom by remember { mutableStateOf(false) }
        var expandedTo by remember { mutableStateOf(false) }

        var fromCurrency by remember { mutableStateOf("") }
        var toCurrency by remember { mutableStateOf("") }

        // Adjusted DropdownMenu to use the expanded state
        Row {


                Button(onClick = { expandedFrom = true }) {
                    Text(if (fromCurrency.isEmpty()) "From" else fromCurrency)
                }

                DropdownMenu(
                    expanded = expandedFrom,
                    onDismissRequest = { expandedFrom = false },
                    modifier = Modifier.padding(8.dp)
                ) {
                    currencySymbols.keys.forEach { currencyCode ->
                        DropdownMenuItem(onClick = {
                            fromCurrency = currencyCode
                            expandedFrom = false
                        }) {
                            Text(text = "$currencyCode - ${currencySymbols[currencyCode]}")
                        }
                    }
                }


            Spacer(modifier = Modifier.width(10.dp))
            // Convert button with the action logic
            Button(
                onClick = {
                    // Placeholder for conversion logic
                    // Ensure you implement the conversion logic in the ViewModel
                },
            ) {
                Text("Convert")
            }
            Spacer(modifier = Modifier.width(10.dp))

            Button(onClick = { expandedTo = true }) {
                Text(if (toCurrency.isEmpty()) "To" else toCurrency)
            }

            DropdownMenu(
                expanded = expandedTo,
                onDismissRequest = { expandedTo = false },
                modifier = Modifier.padding(8.dp)
            ) {
                currencySymbols.keys.forEach { currencyCode ->
                    DropdownMenuItem(onClick = {
                        toCurrency = currencyCode
                        expandedTo = false
                    }) {
                        Text(text = "$currencyCode - ${currencySymbols[currencyCode]}")
                    }
                }
            }
        }


    }


}