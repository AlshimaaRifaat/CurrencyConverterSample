package com.example.currencyconvertersample.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyconvertersample.MainCoroutineRule
import com.example.currencyconvertersample.getOrAwaitValue
import com.example.currencyconvertersample.model.CurrencySymbolsResponse
import com.example.currencyconvertersample.model.LatestRatesResponse
import com.example.currencyconvertersample.repository.CurrencyRepository
import com.example.currencyconvertersample.utils.NetworkHelper
import com.example.currencyconvertersample.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody


@ExperimentalCoroutinesApi
class CurrencyConverterViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CurrencyConverterViewModel
    private lateinit var currencyRepository: CurrencyRepository
    private lateinit var networkHelper: NetworkHelper

    @Before
    fun setup() {
        currencyRepository = mockk()
        networkHelper = mockk()
        viewModel = CurrencyConverterViewModel(currencyRepository, networkHelper)
    }

    @Test
    fun `load currency symbols success`() = mainCoroutineRule.runBlockingTest {
        val fakeResponseData = CurrencySymbolsResponse(success = true, symbols = mapOf("USD" to "United States Dollar"))
        val fakeResponse = Response.success(fakeResponseData)

        coEvery { currencyRepository.getCurrencySymbols(any()) } returns fakeResponse
        coEvery { networkHelper.isNetworkAvailable() } returns true

        viewModel.loadCurrencySymbols()


        assertTrue(viewModel.currencySymbolsResponse.value is Resource.Success)
        assertEquals((viewModel.currencySymbolsResponse.value as Resource.Success).data, fakeResponseData)
    }

    @Test
    fun `load currency symbols failure`() = mainCoroutineRule.runBlockingTest {
        val errorCode = 400 // Bad Request
        val errorMessage = "Error"
        val errorResponseBody = "{\"error\": \"$errorMessage\"}".toResponseBody("application/json".toMediaTypeOrNull())
        val errorResponse = Response.error<CurrencySymbolsResponse>(errorCode, errorResponseBody)
        coEvery { currencyRepository.getCurrencySymbols(any()) } returns errorResponse
        coEvery { networkHelper.isNetworkAvailable() } returns true

        viewModel.loadCurrencySymbols()

        assert(viewModel.currencySymbolsResponse.value is Resource.Error)
    }

    @Test
    fun `fetch latest rates success`() = mainCoroutineRule.runBlockingTest {
        val fakeRatesResponse = LatestRatesResponse(success = true, base = "EUR", rates = mapOf("USD" to 1.0),date="2024-02-15",timestamp = 1519296206)
        coEvery { currencyRepository.getLatestRates(any(),any()) } returns Response.success(fakeRatesResponse)
        coEvery { networkHelper.isNetworkAvailable() } returns true

        viewModel.fetchLatestRates("USD")
        assertTrue(viewModel.latestCurrenciesResponse.getOrAwaitValue() is Resource.Success)
    }

    @Test
    fun `fetch latest rates failure`() = mainCoroutineRule.runBlockingTest {
        val errorCode = 400
        val errorMessage = "Error"
        val errorResponseBody = "{\"error\": \"$errorMessage\"}".toResponseBody("application/json".toMediaTypeOrNull())
        val errorResponse = Response.error<LatestRatesResponse>(errorCode, errorResponseBody)

        coEvery { currencyRepository.getLatestRates(any(), any()) } returns errorResponse
        coEvery { networkHelper.isNetworkAvailable() } returns true

        viewModel.fetchLatestRates("EUR")

        assert(viewModel.latestCurrenciesResponse.getOrAwaitValue() is Resource.Error)
    }

    @Test
    fun `convert currency successfully`() {
        viewModel.amount.value = "100"
        viewModel.fromCurrency.value = "USD"
        viewModel.toCurrency.value = "EUR"
        viewModel._latestCurrenciesResponse.value = Resource.Success(LatestRatesResponse(success = true, rates = mapOf("EUR" to 0.85),date="2024-02-15",timestamp = 1519296206, base = "EUR"))

        viewModel.convertCurrency()

        assertEquals("85.0", viewModel.convertedAmount.value)
    }

    @Test
    fun `swap currencies updates values correctly`() {
        viewModel.fromCurrency.value = "USD"
        viewModel.toCurrency.value = "EUR"

        viewModel.swapCurrencies(viewModel.fromCurrency.value, viewModel.toCurrency.value)

        assertEquals("EUR", viewModel.fromCurrency.value)
        assertEquals("USD", viewModel.toCurrency.value)
    }

    @Test
    fun `on amount changed updates amount and converts currency`() {
        viewModel._latestCurrenciesResponse.value = Resource.Success(LatestRatesResponse(success = true, rates = mapOf("EUR" to 0.85),date="2024-02-15",timestamp = 1519296206, base = "EUR"))
        viewModel.toCurrency.value = "EUR"

        viewModel.onAmountChanged("100")

        assertEquals("100", viewModel.amount.value)
        assertEquals("85.0", viewModel.convertedAmount.value)
    }


}