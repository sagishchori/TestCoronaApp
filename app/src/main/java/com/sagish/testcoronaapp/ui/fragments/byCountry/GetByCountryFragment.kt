package com.sagish.testcoronaapp.ui.fragments.byCountry

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagish.testcoronaapp.R
import com.sagish.testcoronaapp.databinding.DetailsFragmentBinding
import com.sagish.testcoronaapp.ui.adapters.DetailsListAdapter
import com.sagish.testcoronaapp.ui.helpers.AlertDialogHelper
import com.sagish.testcoronaapp.ui.models.CoronaDetailsByCountry
import java.util.*

open class GetByCountryFragment : Fragment() {

    private val viewModel: GetByCountryViewModel by activityViewModels()
    protected lateinit var binding: DetailsFragmentBinding
    protected lateinit var adapter: DetailsListAdapter

    private val calendar : Calendar by lazy {
        Calendar.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.details_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycler()

        setUpSpinner(viewModel)

        setUpDateViewsClickListeners(viewModel)

        setUpObservers(viewModel)
    }

    protected open fun setUpDateViewsClickListeners(viewModel: GetByCountryViewModel) {
        binding.fromText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                if (MotionEvent.ACTION_UP == p1!!.action) {
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePicker = DatePickerDialog(
                        requireContext(),
                        object : DatePickerDialog.OnDateSetListener {
                            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                                val month = p2.plus(1)
                                var day = p3.toString()
                                if (p3 < 10)
                                    day = "0$p3"
                                viewModel.setFromDate("$p1-$month-$day", requireContext())
                            }
                        },
                        year,
                        month,
                        day
                    )
                    datePicker.show()
                }

                return true
            }
        })

        binding.toText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                if (MotionEvent.ACTION_UP == p1!!.action) {
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePicker = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                            val month = p2.plus(1)
                            var day = p3.toString()
                            if (p3 < 10)
                                day = "0$p3"
                            viewModel.setToDate("$p1-$month-$day", requireContext())
                        }
                    }, year, month, day)
                    datePicker.show()
                }

                return true
            }
        })
    }

    protected open fun setUpSpinner(viewModel: GetByCountryViewModel) {
        binding.countryChooserSpinner.onItemSelectedListener = viewModel
    }

    protected open fun setUpObservers(viewModel: GetByCountryViewModel) {
        viewModel.getFromDate().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String?) {
                binding.fromText.setText(t)
            }
        })

        viewModel.getToDate().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String?) {
                binding.toText.setText(t)
            }
        })

        viewModel.getErrorMessage().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String?) {
                AlertDialogHelper.show(requireContext(), t!!, R.string.ok)
            }
        })

        viewModel.getCoronaDetailsByCountry().observe(viewLifecycleOwner, object : Observer<CoronaDetailsByCountry> {
            override fun onChanged(t: CoronaDetailsByCountry?) {
                adapter.setData(t!!.casesByTypeAndDate)
            }
        })
    }

    /**
     * Set up the initial state of recyclerview.
     */
    protected open fun setUpRecycler() {
        val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.layoutManager = lm

        val itemDecoration = DividerItemDecoration(requireContext(), lm.orientation)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider, null))
        binding.recycler.addItemDecoration(itemDecoration)

        adapter = DetailsListAdapter()
        binding.recycler.adapter = adapter
    }
}