package com.example.currencyconvertersample.ui.bottom_nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertersample.model.HistoricalDataResponse
import com.example.currencyconvertersample.view_model.HistoricalDataViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
 fun HistoricalDataScreen(base:String, viewModel: HistoricalDataViewModel = hiltViewModel()) {
     LaunchedEffect(key1=true){
         //Hint: we should pass "base" instead of "EUR" but actually API restricted with only "EUR" and doesn't work for other currency symbols
         viewModel.fetchHistoricalDataForLastThreeDays("EUR")
     }
     val ratesData by viewModel.ratesData.collectAsState()

    LazyColumn {
        ratesData.groupBy { it.date }.forEach { (date, historicalDataList) ->
            item {
                Text(text = "Date: $date", color = Color.Blue, style = TextStyle(
                    fontSize = 22.sp
                ))
            }
            items(historicalDataList) { historicalData ->
                HistoricalDataItem(historicalData)
            }
        }
    }
}

@Composable
fun HistoricalDataItem(historicalData: HistoricalDataResponse) {
    historicalData.rates.forEach {(currency, rate) ->
        Text("$currency: $rate")

    }


}
