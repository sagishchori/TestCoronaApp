package com.sagish.testcoronaapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sagish.testcoronaapp.ui.fragments.byCountry.GetByCountryViewModel
import com.sagish.testcoronaapp.ui.fragments.myCountry.GetByMyCountryViewModel
import com.sagish.testcoronaapp.ui.fragments.nearBy.NearByViewModel
import com.sagish.testcoronaapp.ui.helpers.AlertDialogHelper
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val providerReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            // GPS may be enabled by the user
            if (LocationManager.PROVIDERS_CHANGED_ACTION == p1!!.action) {

            }
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            // Bluetooth may be enabled by the user
            if (BluetoothAdapter.ACTION_STATE_CHANGED == p1!!.action) {
                if (p1.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
                    val state = p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    if (state == BluetoothAdapter.STATE_ON ||
                        state == BluetoothAdapter.STATE_TURNING_ON) {
                        nearByViewModel.setBluetoothEnabled(true)
                    } else if (state == BluetoothAdapter.STATE_OFF ||
                            state == BluetoothAdapter.STATE_TURNING_OFF){
                        nearByViewModel.setBluetoothEnabled(false)
                    }
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    private val byCountryViewModel: GetByCountryViewModel by viewModels()
    private val byMyCountryViewModel: GetByMyCountryViewModel by viewModels()
    private val nearByViewModel: NearByViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.by_country, R.id.my_country, R.id.near_by))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setUpLocationPermissionsAndManagement()

        setUpBluetoothPermissionsAndManagement()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    nearByViewModel.setBluetoothEnabled(true)
                } else {
                    nearByViewModel.setBluetoothEnabled(false)
                }
            }
        }
    }

    private fun setUpBluetoothPermissionsAndManagement() {

        // First' we check if the device has Bluetooth or support it
        if (bluetoothAdapter == null) {
            nearByViewModel.setHasBluetooth(false)
            return
        } else {
            nearByViewModel.setHasBluetooth(true)
        }

        // Next we need to ensure that Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            nearByViewModel.setBluetoothEnabled(true)
        }
    }

    private fun setUpLocationPermissionsAndManagement() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if GPS is enabled
        val hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // GPS is enabled
        if (hasGPS) {

            // Check for location permissions
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        currentLocation = it
                        val geoCoder = Geocoder(this, Locale.ENGLISH)
                        val address = geoCoder.getFromLocation(
                            currentLocation!!.latitude,
                            currentLocation!!.longitude,
                            1
                        )
                        byMyCountryViewModel.setCurrentCountry(address[0].countryName)
                    } else {
                        val locationRequest = LocationRequest.create()?.apply {
                            interval = 1000
                            fastestInterval = 5000
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        }

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    }
                }
            } else {

                // If no location permission is enabled request one
                requestForegroundPermissions()
            }
        }

        // GPS is disabled, notify to user to enabled it
        else {
            AlertDialogHelper.show(this, R.string.gps_disabled, R.string.ok)
        }
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(providerReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(providerReceiver)
        unregisterReceiver(bluetoothReceiver)
    }

    private fun requestForegroundPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE -> {

                // If no permission granted
                if(grantResults.isEmpty()) {
                    AlertDialogHelper.show(this, R.string.must_enable_gps, R.string.ok)
                }

                // If permission granted
                else if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE = 12
        const val REQUEST_ENABLE_BT = 13
    }
}