package com.sagish.testcoronaapp.ui.fragments.myCountry

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.sagish.testcoronaapp.ui.fragments.byCountry.GetByCountryFragment
import com.sagish.testcoronaapp.ui.fragments.byCountry.GetByCountryViewModel

class GetByMyCountryFragment : GetByCountryFragment() {

    private val viewModel: GetByMyCountryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpRecycler()

        setUpSpinner(viewModel)

        setUpDateViewsClickListeners(viewModel)

        setUpObservers(viewModel)
    }

    override fun setUpSpinner(viewModel: GetByCountryViewModel) {
        binding.countryChooserSpinner.isEnabled = false

        val list = ArrayList<String>()
        list.add((viewModel as GetByMyCountryViewModel).getCountry()!!)
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, list)

        binding.countryChooserSpinner.adapter = adapter
    }
}