package com.sagish.testcoronaapp.ui.fragments.byCountry

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sagish.testcoronaapp.R
import com.sagish.testcoronaapp.api.repositories.CountryDetailsRepository
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountry
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountryItem
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import retrofit2.HttpException
import retrofit2.http.HTTP
import java.net.HttpURLConnection
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class GetByCountryViewModel : ViewModel(), AdapterView.OnItemSelectedListener {

    protected open val toDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    protected open val fromDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    protected open val coronaDetailsByCountry = MutableLiveData<CoronaDetailsByCountry>()

    protected open val error = MutableLiveData<String>()

    protected open val country = MutableLiveData<String>()

    protected open val repo : CountryDetailsRepository = CountryDetailsRepository()

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 == 0) {
            country.value = ""
        } else {
            country.value = (p1 as AppCompatTextView).text.toString()
            tryToFetchData()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    /**
     * Try to fetch the data from web.
     */
    protected open fun tryToFetchData() {
        if (country.value!!.isNotEmpty() and fromDate.value!!.isNotEmpty() and toDate.value!!.isNotEmpty()) {
            val deferredList = ArrayList<Deferred<*>>()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                when (error) {
                    is UnknownHostException -> {
                        this.error.postValue(error.message)
                    }

                    is HttpException -> {
                        if (error.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                            this.error.postValue("Not found")
                        }
                    }
                }
            }
            val errorHandlingScope = CoroutineScope(errorHandler)
            errorHandlingScope.launch(Dispatchers.IO) {
                deferredList.add(async {
                    repo.fetchCoronaDetailsForCountryAndDates(country.value!!,
                        fromDate.value!!,
                        toDate.value!!,
                        CoronaDetailsByCountryItem.CoronaStatus.Confirmed)
                })

                deferredList.add(async {
                    repo.fetchCoronaDetailsForCountryAndDates(country.value!!,
                        fromDate.value!!,
                        toDate.value!!,
                        CoronaDetailsByCountryItem.CoronaStatus.Recovered)
                })

                deferredList.add(async {
                    repo.fetchCoronaDetailsForCountryAndDates(country.value!!,
                        fromDate.value!!,
                        toDate.value!!,
                        CoronaDetailsByCountryItem.CoronaStatus.Deaths)
                })

                try {
                    deferredList.awaitAll()
                    processData(deferredList)
                } catch (e : CancellationException) {
                    throw e
                }
            }
        }
    }

    protected open fun processData(deferredList: ArrayList<Deferred<*>>) {
        val item = CoronaDetailsByCountry(country.value!!)
        deferredList.forEach { deferred ->
            val list = deferred.getCompleted() as ArrayList<CoronaDetailsByCountryItem>
            list.forEach {
                val date = it.Date
                var map : HashMap<String, Int>
                if (item.casesByTypeAndDate[date] != null) {
                    map = item.casesByTypeAndDate[date]!!
                } else {
                    map = HashMap()
                }

                map[it.Status] = it.Cases
                item.casesByTypeAndDate[date] = map
            }
        }
        coronaDetailsByCountry.postValue(item)
    }

    /**
     * Set the start date of the search.
     */
    fun setFromDate(date: String, context: Context) {
        fromDate.value = date
        if (validateBeforeOrAfter(date, toDate.value!!) || toDate.value!!.isEmpty()) {
            tryToFetchData()
        } else {
            toDate.value = date
            error.value = context.resources.getString(R.string.date_after_error_message)
        }
    }

    /**
     * Return the start date of the search as LiveData to be able to observe the changes and act accordingly.
     */
    fun getFromDate() : LiveData<String> {
        return fromDate
    }

    /**
     * Set the end date of the search.
     */
    fun setToDate(date: String, context : Context) {
        toDate.value = date
        if (validateBeforeOrAfter(fromDate.value ?: "", date) || fromDate.value!!.isEmpty()) {
            tryToFetchData()
        } else {
            fromDate.value = date
            error.value = context.resources.getString(R.string.date_before_error_message)
        }
    }

    /**
     * Return the end date of the search as LiveData to be able to observe the changes and act accordingly.
     */
    fun getToDate() : LiveData<String> {
        return toDate
    }

    /**
     * Validate the start and end dates of the search.
     * If the start date is after the end date or the end date is before the start date an
     * error will be shown to user accordingly.
     */
    private fun validateBeforeOrAfter(fromDate: String, toDate: String) : Boolean {
        var date1 = Date()
        var date2 = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        try {
            date1 = formatter.parse(fromDate)
            date2 = formatter.parse(toDate)
        } catch (e: ParseException) {

        }

        return date1.before(date2)
    }

    /**
     * Get the error as a LiveData to update the view accordingly.
     */
    fun getErrorMessage() : LiveData<String> {
        return error
    }

    /**
     * Get the Corona details of the requested country as a LiveData to update the view accordingly.
     */
    fun getCoronaDetailsByCountry() : LiveData<CoronaDetailsByCountry> {
        return coronaDetailsByCountry
    }
}