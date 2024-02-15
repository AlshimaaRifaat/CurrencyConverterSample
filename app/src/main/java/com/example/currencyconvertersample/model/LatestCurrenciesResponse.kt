package com.example.currencyconvertersample.model

data class LatestCurrenciesResponse(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)