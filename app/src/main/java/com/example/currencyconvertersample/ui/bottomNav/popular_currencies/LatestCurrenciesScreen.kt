package com.example.currencyconvertersample.ui.bottomNav.popular_currencies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertersample.model.LatestCurrenciesResponse
import com.example.currencyconvertersample.utils.Resource
import com.example.currencyconvertersample.view_model.CurrencyViewModel

@Composable
fun PopularCurrenciesScreen(viewModel: CurrencyViewModel = hiltViewModel()) {
    // Observing LiveData from ViewModel
    val currencyResponse by viewModel.latestCurrenciesResponse.observeAsState(initial = Resource.Loading())

    Column(modifier = Modifier.padding(16.dp)) {
        when (currencyResponse) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Success -> {
                val data = (currencyResponse as Resource.Success<LatestCurrenciesResponse>).data
                Text(
                    text = "Base Currency: ${data?.base}",
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatesList(rates = data!!.rates)
            }
            is Resource.Error -> {
                Text(
                    text = "Error: ${(currencyResponse as Resource.Error).message}",
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
fun RatesList(rates: Map<String, Double>) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(rates.toList()) { rate ->
            RateItem(currency = rate.first, value = rate.second)
        }
    }
}

@Composable
fun RateItem(currency: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = currency, style = MaterialTheme.typography.body1)
        Text(text = "%.2f".format(value), style = MaterialTheme.typography.body1)
    }
    Divider()

}