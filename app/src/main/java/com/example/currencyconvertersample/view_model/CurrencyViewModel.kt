package com.example.currencyconvertersample.view_model


import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import com.example.currencyconvertersample.model.LatestCurrenciesResponse
import com.example.currencyconvertersample.utils.NetworkHelper
import com.example.currencyconvertersample.repository.CurrencyRepository
import com.example.currencyconvertersample.utils.API_KEY
import com.example.currencyconvertersample.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    var currencySymbolsResponse = mutableStateOf<Resource<CurrencySymbolsResponse>>(Resource.Loading())
        private set

  /*  var errorMessage = mutableStateOf<String?>(null)
        private set*/

    private val _latestCurrenciesResponse = MutableLiveData<Resource<LatestCurrenciesResponse>>()
     val latestCurrenciesResponse: LiveData<Resource<LatestCurrenciesResponse>> = _latestCurrenciesResponse


    var convertedAmount = mutableStateOf<String>("")
        private set

    var amount = mutableStateOf<String>("1")
        private set

    var fromCurrency = mutableStateOf<String>("")
        private set
    var toCurrency = mutableStateOf<String>("")
        private set

    init {
        loadCurrencySymbols()
        fetchLatestCurrencies()
    }

    private fun loadCurrencySymbols() {
        viewModelScope.launch {
            try {
                if (isNetworkAvailable()) {
                    val response = repository.getCurrencySymbols(API_KEY)
                        currencySymbolsResponse.value = if(response.body()?.success==true) {
                            Resource.Success(response.body()!!)
                        } else {
                            Resource.Error("You have not supplied a valid API Access Key. [Technical Support: support@apilayer.com]")
                        }

                } else {
                    currencySymbolsResponse.value = Resource.Error("No internet connection")
                }
            } catch (e: Exception) {
                currencySymbolsResponse.value= Resource.Error("Failed to load currency symbols: ${e.message}")
            }
        }
    }

    private fun fetchLatestCurrencies() = viewModelScope.launch {
        _latestCurrenciesResponse.postValue(Resource.Loading())
        try {
            val response = repository.getLatestCurrencies(API_KEY)
            if(response.isSuccessful && response.body()?.success==true){
                _latestCurrenciesResponse.postValue(Resource.Success(response.body()!!))
            }else{
                _latestCurrenciesResponse.postValue(Resource.Error("You have not supplied a valid API Access Key. [Technical Support: support@apilayer.com]"))
            }

        }catch (e: Exception) {
            _latestCurrenciesResponse.postValue(Resource.Error("No internet connection"))
        }
    }

   private fun isNetworkAvailable(): Boolean {
       return networkHelper.isNetworkAvailable()
        }


    /*fun reloadData() {
        loadCurrencySymbols()
    }
*/



    fun onAmountChanged(newAmount: String) {
        amount.value = newAmount
        convertCurrency()
    }

    fun convertCurrency(){
        val rate = getConversionRate(fromCurrency.value, toCurrency.value)
        val result = amount.value.toDoubleOrNull()?.times(rate) ?: 0.0
        convertedAmount.value = result.toString()
    }

    fun onConvertedAmountChanged(newConvertedAmount: String) {
        convertedAmount.value = newConvertedAmount
        val rate = getConversionRate(toCurrency.value,fromCurrency.value)
        val result=  newConvertedAmount.toDoubleOrNull()?.times(rate)?: 0.0
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

    fun swapCurrencies(fromCurrency: String, toCurrency: String) {
      // val temp = fromC
    }





}
