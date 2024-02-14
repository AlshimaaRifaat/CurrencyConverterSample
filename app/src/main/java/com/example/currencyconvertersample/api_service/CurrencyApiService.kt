package com.example.currencyconvertersample.api_service

import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("symbols")
    suspend fun getCurrencySymbols(@Query("access_key") apiKey: String): Response<CurrencySymbolsResponse>
}
