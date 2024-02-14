package com.example.currencyconvertersample.view_model


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertersample.helper.NetworkHelper
import com.example.currencyconvertersample.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    var currencySymbols = mutableStateOf<Map<String, String>>(mapOf())
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        loadCurrencySymbols()
    }

    private fun loadCurrencySymbols() {
        viewModelScope.launch {
            try {
                if (isNetworkAvailable()) {
                    // Assume you have an API key
                    val apiKey = "5a8dfda0e2efa71172799eace09bfd52"
                    val result = repository.getCurrencySymbols(apiKey)
                    currencySymbols.value = result ?: mapOf()
                    errorMessage.value = null // Reset error message on successful load
                } else {
                    errorMessage.value = "No internet connection"
                }
            } catch (e: Exception) {
                // Handle the error, e.g., by setting an error message
                errorMessage.value = "Failed to load currency symbols: ${e.message}"
                // Optionally, log the error or handle it as needed for your use case
            }
        }
    }

   private fun isNetworkAvailable(): Boolean {
       return networkHelper.isNetworkAvailable()
        }


    // You might want to expose a method to reload data in case of an error
    fun reloadData() {
        loadCurrencySymbols()
    }
}
