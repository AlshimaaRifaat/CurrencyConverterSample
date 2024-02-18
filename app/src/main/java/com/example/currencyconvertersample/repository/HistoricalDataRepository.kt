package com.example.currencyconvertersample.repository

import com.example.currencyconvertersample.api_service.CurrencyApiService
import com.example.currencyconvertersample.model.HistoricalDataResponse
import retrofit2.Response
import javax.inject.Inject

class HistoricalDataRepository @Inject constructor(private val apiService: CurrencyApiService) {
    suspend fun getHistoricalData(date:String,accessKey:String,base:String) : Response<HistoricalDataResponse> {
        return apiService.getHistoricalData(date=date,accessKey=accessKey, base = base)
    }
}