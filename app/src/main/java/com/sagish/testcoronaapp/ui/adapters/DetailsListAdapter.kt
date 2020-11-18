package com.sagish.testcoronaapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagish.testcoronaapp.databinding.DetailsListItemViewBinding
import com.sagish.testcoronaapp.ui.views.DetailsListItemView

class DetailsListAdapter : RecyclerView.Adapter<DetailsListItemView>() {

    private var itemList = LinkedHashMap<String, HashMap<String, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsListItemView {
        val binding = DetailsListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsListItemView(binding)
    }

    override fun onBindViewHolder(holder: DetailsListItemView, position: Int) {
        val key = itemList.keys.elementAt(position)
        val listItem = itemList[key]
        holder.bind(listItem!!, key)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setData(itemList: LinkedHashMap<String, HashMap<String, Int>>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }
}