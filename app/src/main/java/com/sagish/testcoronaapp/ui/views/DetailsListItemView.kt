package com.sagish.testcoronaapp.ui.views

import androidx.recyclerview.widget.RecyclerView
import com.sagish.testcoronaapp.databinding.DetailsListItemViewBinding
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountryItem

class DetailsListItemView(private val binding: DetailsListItemViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: /*CoronaDetailsByCountryItem*/HashMap<String, Int>, date: String) {
        binding.date = date.split("T")[0]
        val details = Details(item[CoronaDetailsByCountryItem.CoronaStatus.Confirmed.getValue()]!!,
            item[CoronaDetailsByCountryItem.CoronaStatus.Recovered.getValue()]!!,
            item.get(CoronaDetailsByCountryItem.CoronaStatus.Deaths.getValue())!!)
        binding.details = details
    }

    class Details(val confirmed : Int,
                          val recovered : Int,
                          val deaths : Int)
}