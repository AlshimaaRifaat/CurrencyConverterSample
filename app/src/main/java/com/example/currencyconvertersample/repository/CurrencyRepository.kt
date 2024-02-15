package com.example.currencyconvertersample.repository

import com.example.currencyconvertersample.api_service.CurrencyApiService
import com.example.currencyconvertersample.model.LatestCurrenciesResponse
import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import retrofit2.Response
import javax.inject.Inject

class CurrencyRepository @Inject constructor(private val apiService: CurrencyApiService) {
    suspend fun getCurrencySymbols(apiKey: String): Response<CurrencySymbolsResponse> {
         return apiService.getCurrencySymbols(apiKey)
    }

    suspend fun getLatestCurrencies(accessKey: String): Response<LatestCurrenciesResponse> =
        apiService.getLatestCurrencies(accessKey)
}
