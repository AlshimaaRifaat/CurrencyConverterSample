package com.example.currencyconvertersample.api_service

import com.example.currencyconvertersample.model.LatestRatesResponse
import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import com.example.currencyconvertersample.model.HistoricalDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("symbols")
    suspend fun getCurrencySymbols(@Query("access_key") apiKey: String): Response<CurrencySymbolsResponse>
    @GET("latest")
    suspend fun getLatestRates(@Query("base") base: String,@Query("access_key") accessKey: String): Response<LatestRatesResponse>

    @GET("{date}")
    suspend fun getHistoricalData(@Path("date") date:String, @Query("access_key") accessKey: String,@Query("base") base:String): Response<HistoricalDataResponse>
}

