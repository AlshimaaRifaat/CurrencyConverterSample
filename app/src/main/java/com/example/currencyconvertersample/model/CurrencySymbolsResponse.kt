package com.example.currencyconvertersample.model

data class CurrencySymbolsResponse(
    val success: Boolean,
    val symbols: Map<String, String>
)
