package com.example.currencyconvertersample.view_model


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

    var convertedAmount = mutableStateOf<String>("")
        private set

    var amount = mutableStateOf<String>("1")
        private set

    init {
        loadCurrencySymbols()
    }

    private fun loadCurrencySymbols() {
        viewModelScope.launch {
            try {
                if (isNetworkAvailable()) {
                    val apiKey = "ddcc54d739557a79b5b81c5e75a9b13a"
                    val result = repository.getCurrencySymbols(apiKey)
                    currencySymbols.value = result ?: mapOf()
                    errorMessage.value = null // Reset error message on successful load
                } else {
                    errorMessage.value = "No internet connection"
                }
            } catch (e: Exception) {
                errorMessage.value = "Failed to load currency symbols: ${e.message}"

            }
        }
    }

   private fun isNetworkAvailable(): Boolean {
       return networkHelper.isNetworkAvailable()
        }


    /*fun reloadData() {
        loadCurrencySymbols()
    }
*/



    fun onAmountChanged(newAmount: String, fromCurrency: String, toCurrency: String) {
        amount.value = newAmount
        val rate = getConversionRate(fromCurrency, toCurrency)
        val result = newAmount.toDoubleOrNull()?.times(rate) ?: 0.0
        convertedAmount.value = result.toString()
    }

    fun onConvertedAmountChanged(newConvertedAmount: String, fromCurrency: String, toCurrency: String) {
        convertedAmount.value = newConvertedAmount
        val rate = getConversionRate(fromCurrency,toCurrency)
        val result=  newConvertedAmount.toDoubleOrNull()?.div(rate)?: 0.0
        amount.value = result.toString()

    }
    private val conversionRates = mapOf(
        "USD_TO_EUR" to 0.85,
        "EUR_TO_USD" to 1.18,
        "USD_TO_JPY" to 110.0,
        "JPY_TO_USD" to 0.0091,
        "EUR_TO_GBP" to 0.86,
        "GBP_TO_EUR" to 1.16,
        "USD_TO_GBP" to 0.73,
        "GBP_TO_USD" to 1.37, // Example rate
        // Add more based on necessity
    )

    private fun getConversionRate(fromCurrency: String, toCurrency: String): Double {
        val key = "${fromCurrency}_TO_$toCurrency"
        return conversionRates[key] ?: 2.0
    }

    fun swapCurrencies() {
  /*      val temp = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = temp
        // Trigger conversion calculation
        onAmountChanged(amount.value, fromCurrency.value, toCurrency.value)*/
    }


}
