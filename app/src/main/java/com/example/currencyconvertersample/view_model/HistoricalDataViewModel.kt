package com.example.currencyconvertersample.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertersample.model.HistoricalDataResponse
import com.example.currencyconvertersample.repository.CurrencyRepository
import com.example.currencyconvertersample.repository.HistoricalDataRepository
import com.example.currencyconvertersample.utils.API_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class HistoricalDataViewModel @Inject constructor(private val historicalDataRepository: HistoricalDataRepository) : ViewModel(){

    private val _ratesData = MutableStateFlow<List<HistoricalDataResponse>>(emptyList())
    val ratesData : StateFlow<List<HistoricalDataResponse>> = _ratesData.asStateFlow()


    @RequiresApi(Build.VERSION_CODES.O)
     fun fetchHistoricalDataForLastThreeDays(base: String){
         print("sh base of his: $base")
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        val dates = listOf(0, 1, 2).map { today.minusDays(it.toLong()).format(formatter) }
        print("sh date of his: ${dates.toString()}")
        viewModelScope.launch {
            val response = dates.map { date ->
             async { historicalDataRepository.getHistoricalData(date, API_KEY, base) }

            }.awaitAll()

            _ratesData.value = response.filter { it.body()!!.success }.mapNotNull { it.body() }

        }
    }

}