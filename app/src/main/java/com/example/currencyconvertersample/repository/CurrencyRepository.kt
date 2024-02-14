package com.example.currencyconvertersample.repository

import com.example.currencyconvertersample.api_service.CurrencyApiService
import javax.inject.Inject

class CurrencyRepository @Inject constructor(private val apiService: CurrencyApiService) {
    suspend fun getCurrencySymbols(apiKey: String): Map<String, String>? {
        val response = apiService.getCurrencySymbols(apiKey)
        if (response.isSuccessful) {
            return response.body()?.symbols
        }
        return null
    }
}
