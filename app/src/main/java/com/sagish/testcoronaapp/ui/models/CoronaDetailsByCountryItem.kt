package com.sagish.testcoronaapp.ui.models

data class CoronaDetailsByCountryItem(
    val Cases: Int,
    val City: String,
    val CityCode: String,
    val Country: String,
    val CountryCode: String,
    val Date: String,
    val Lat: String,
    val Lon: String,
    val Province: String,
    val Status: String
) {

    var casesByType = HashMap<CoronaStatus, Int>()

    enum class CoronaStatus(private val status: String) {
        Confirmed("confirmed"),
        Recovered("recovered"),
        Deaths("deaths");

        fun getValue() : String {
            return this.status
        }
    }
}