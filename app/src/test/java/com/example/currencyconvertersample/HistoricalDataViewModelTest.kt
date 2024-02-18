import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.Mockito.`when`
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.junit.rules.TestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertersample.MainCoroutineRule
import com.example.currencyconvertersample.model.HistoricalDataResponse
import com.example.currencyconvertersample.repository.HistoricalDataRepository
import com.example.currencyconvertersample.utils.API_KEY
import com.example.currencyconvertersample.view_model.HistoricalDataViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class HistoricalDataViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var historicalDataRepository: HistoricalDataRepository

    private lateinit var viewModel: HistoricalDataViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = HistoricalDataViewModel(historicalDataRepository)
    }


    @Test
    fun `handle API error`() = mainCoroutineRule.runTest {

        val errorMessage = "Error fetching data"
        val date = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        coEvery { historicalDataRepository.getHistoricalData(date, API_KEY, "EUR") } returns Response.error(400, errorMessage.toResponseBody("text/plain".toMediaTypeOrNull()))


        viewModel.fetchHistoricalDataForLastThreeDays("GBP")


        val results = viewModel.ratesData.value


        assertTrue(results.isEmpty())
        coVerify(exactly = 1) { historicalDataRepository.getHistoricalData(any(), any(), any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}



