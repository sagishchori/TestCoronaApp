package com.sagish.testcoronaapp.api.repositories

import com.sagish.testcoronaapp.api.RetrofitClient
import com.sagish.testcoronaapp.api.services.FetchCoronaDetailsForCountry
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountryItemsList
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountryItem
import kotlin.Exception

class CountryDetailsRepository {

    suspend fun fetchCoronaDetailsForCountryAndDates(country: String,
                                                     fromDate: String,
                                                     toDate: String,
                                                     status: CoronaDetailsByCountryItem.CoronaStatus) : CoronaDetailsByCountryItemsList? {

        val service = RetrofitClient.buildService(FetchCoronaDetailsForCountry::class.java)

        return try {
            service!!.getCoronaDetailsForCountryByTypeAndDate(
                country,
                status.name.toLowerCase(),
                fromDate,
                toDate
            )
        } catch (e : Exception) {
            throw e
        }
    }
}