package com.example.currencyconvertersample.view_model


import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import com.example.currencyconvertersample.model.LatestRatesResponse
import com.example.currencyconvertersample.utils.NetworkHelper
import com.example.currencyconvertersample.repository.CurrencyRepository
import com.example.currencyconvertersample.utils.API_KEY
import com.example.currencyconvertersample.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    var currencySymbolsResponse = mutableStateOf<Resource<CurrencySymbolsResponse>>(Resource.Loading())
        private set


    private val _latestCurrenciesResponse = MutableLiveData<Resource<LatestRatesResponse>>()
     val latestCurrenciesResponse: LiveData<Resource<LatestRatesResponse>> = _latestCurrenciesResponse


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

     fun fetchLatestRates(base: String ? = null,apiKey: String? = API_KEY) = viewModelScope.launch {
        _latestCurrenciesResponse.postValue(Resource.Loading())
        try {
            val response = repository.getLatestRates(base ?: ""  ,apiKey ?: API_KEY)
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




    fun onAmountChanged(newAmount: String) {
        amount.value = newAmount
        convertCurrency()
    }

    fun convertCurrency(){
            val latestResponse = latestCurrenciesResponse.value?.data
            if (latestResponse != null) {
                val rate = latestResponse.rates[toCurrency.value]
                if (rate != null) {
                    val result = amount.value.toDoubleOrNull()?.times(rate) ?: 0.0
                    convertedAmount.value = result.toString()
                }
            } else {
                convertedAmount.value = "0.0"
            }

    }

    fun onConvertedAmountChanged(newConvertedAmount: String) {
        viewModelScope.launch {
            val latestResponse = _latestCurrenciesResponse.value?.data
            if (latestResponse != null) {

                val rate = latestResponse.rates[toCurrency.value]

                if (rate != null) {
                  val result = newConvertedAmount.toDoubleOrNull()?.div(rate) ?: 0.0
                    amount.value = "%.2f".format(result)
                    convertedAmount.value = newConvertedAmount
                } else {
                    convertedAmount.value = "0.0"
                }
            } else {
                convertedAmount.value = "0.0"
            }
        }
    }


    fun swapCurrencies(fromCurrency: String, toCurrency: String) {
        this.fromCurrency.value = toCurrency
        this.toCurrency.value = fromCurrency

        // Update the converted amount based on the new currencies
        convertCurrency()
    }





}
