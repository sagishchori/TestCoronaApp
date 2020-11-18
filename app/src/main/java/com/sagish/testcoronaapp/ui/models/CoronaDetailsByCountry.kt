package com.sagish.testcoronaapp.ui.models

class CoronaDetailsByCountry(val country: String) {
    var casesByTypeAndDate = LinkedHashMap<String, HashMap<String, Int>>()
}