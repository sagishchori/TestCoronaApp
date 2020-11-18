package com.sagish.testcoronaapp.ui.fragments.myCountry

import com.sagish.testcoronaapp.ui.fragments.byCountry.GetByCountryViewModel

class GetByMyCountryViewModel : GetByCountryViewModel() {

    fun setCurrentCountry(currentCountry : String) {
        country.value = currentCountry
    }

    fun getCountry(): String? {
        return country.value
    }
}