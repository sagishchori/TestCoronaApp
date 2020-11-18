package com.sagish.testcoronaapp.ui.fragments.nearBy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NearByViewModel : ViewModel() {

    private var isBluetoothEnabled = MutableLiveData(false)
    private var isScanning = MutableLiveData(false)
    private var hasBlueTooth: Boolean = false

    fun setHasBluetooth(hasBlueTooth: Boolean) {
        this.hasBlueTooth = hasBlueTooth
    }

    fun setBluetoothEnabled(enabled: Boolean) {
        isBluetoothEnabled.postValue(enabled)
    }

    fun hasBluetooth() : Boolean{
        return hasBlueTooth
    }

    fun isBluetoothEnabled() : LiveData<Boolean> {
        return isBluetoothEnabled
    }

    fun setIsScanning(isScanning : Boolean) {
        this.isScanning.value = isScanning
    }

    fun isScanning() : LiveData<Boolean> {
        return isScanning
    }
}