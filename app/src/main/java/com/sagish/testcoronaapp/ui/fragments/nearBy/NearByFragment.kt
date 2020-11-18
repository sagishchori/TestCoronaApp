package com.sagish.testcoronaapp.ui.fragments.nearBy

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.sagish.testcoronaapp.R
import com.sagish.testcoronaapp.databinding.FragmentBluetoothBinding
import com.sagish.testcoronaapp.ui.helpers.AlertDialogHelper

class NearByFragment : Fragment() {

    private val viewModel: NearByViewModel by activityViewModels()
    lateinit var binding : FragmentBluetoothBinding

    private val deviceFoundBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when(p1!!.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device : BluetoothDevice? = p1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        val deviceAddress = device.address
                        if (deviceAddress == "SOME_MAC_ADDRESS") {
                            AlertDialogHelper.show(requireActivity(),
                                R.string.infected_person_found,
                                R.string.ok,
                                object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    // Do some action
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bluetooth, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startScanButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.info.text = resources.getString(R.string.scanning_for_near_by_people)
                viewModel.setIsScanning(true)
                showScanButton(false)
                showStopScanButton(true)
                showProgress(true)
                startBluetoothScan()
            }
        })

        binding.stopScanButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                viewModel.setIsScanning(false)
                startScan(false)
                showScanButton(true)
                showStopScanButton(false)
                showProgress(false)
                binding.info.text = resources.getString(R.string.scan_info)
            }
        })

        setUpObservers(viewModel)
    }


    private fun setUpObservers(viewModel: NearByViewModel) {
        viewModel.isBluetoothEnabled().observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {

                // If Bluetooth disabled in the middle of a scan return the view to its initial state
                if (!t!! && viewModel.isScanning().value!!) {
                    showStopScanButton(false)
                    showProgress(false)
                    showScanButton(true)
                    startScan(false)
                    binding.info.text = resources.getString(R.string.scan_info)
                }
            }
        })
    }

    /**
     * Show or hide the stop scan button
     */
    private fun showStopScanButton(show : Boolean) {
        if (show) {
            binding.stopScanButton.visibility = View.VISIBLE
        } else {
            binding.stopScanButton.visibility = View.INVISIBLE
        }
    }

    /**
     * Show or hide the scan button
     */
    private fun showScanButton(show: Boolean) {
        if (show) {
            binding.startScanButton.visibility = View.VISIBLE
        } else {
            binding.startScanButton.visibility = View.INVISIBLE
        }
    }

    /**
     * Start the flow of Bluetooth device detection.
     */
    private fun startBluetoothScan() {

        // If the device does not support Bluetooth
        if (!viewModel.hasBluetooth()) {

            // Show error message
            AlertDialogHelper.show(requireActivity(), R.string.the_device_does_not_support_bluetooth, R.string.ok)

            // Hide progressBar
            showProgress(false)

            // Show the scan button
            showScanButton(true)

            // Hide the stop scan button
            showStopScanButton(false)

            // Set info text
            binding.info.text = resources.getString(R.string.scan_info)
            return
        }

        // If Bluetooth is not enabled
        if (!viewModel.isBluetoothEnabled().value!!) {

            // Show info message that tells the user to enable Bluetooth
            AlertDialogHelper.show(requireActivity(), R.string.bluetooth_must_be_enabled_for_detection, R.string.ok)

            // Hide progressBar
            showProgress(false)

            // Show the scan button
            showScanButton(true)

            // Hide the stop scan button
            showStopScanButton(false)

            // Set info text
            binding.info.text = resources.getString(R.string.scan_info)
            return
        }

        startScan(true)
    }

    override fun onStart() {
        super.onStart()

        requireActivity().registerReceiver(deviceFoundBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    override fun onStop() {
        super.onStop()

        requireActivity().unregisterReceiver(deviceFoundBroadcastReceiver)
    }

    /**
     * Show progressBar while scanning for BluetoothDevice
     */
    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    /**
     * Start or stop the Bluetooth scan
     */
    private fun startScan(start : Boolean) {
        if (start) {
            BluetoothAdapter.getDefaultAdapter().startDiscovery()
        } else {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
        }
    }
}