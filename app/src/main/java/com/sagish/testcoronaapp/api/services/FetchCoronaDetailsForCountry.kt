package com.sagish.testcoronaapp.api.services

import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountryItemsList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FetchCoronaDetailsForCountry {

    @GET("country/{country}/status/{status}?")
    suspend fun getCoronaDetailsForCountryByTypeAndDate(@Path("country") country : String,
                                                        @Path("status") status : String,
                                                        @Query("from") fromDate : String,
                                                        @Query("to") toDate : String) : CoronaDetailsByCountryItemsList?
}