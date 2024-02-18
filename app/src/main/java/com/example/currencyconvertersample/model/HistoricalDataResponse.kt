package com.example.currencyconvertersample.model

data class HistoricalDataResponse(
    val success: Boolean,
    val historical: Boolean,
    val date: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)
