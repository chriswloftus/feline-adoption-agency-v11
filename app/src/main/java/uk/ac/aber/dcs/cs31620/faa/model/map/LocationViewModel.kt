package uk.ac.aber.dcs.cs31620.faa.model.map

import android.Manifest
import android.app.Application
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.faa.model.util.FAA_TAG

// Adapted from:
// https://medium.com/@codingin254/location-unveiled-a-simple-guide-to-jetpack-compose-for-getting-location-in-your-apps-1ca9f2bbee29
class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application

    // Use the fused location provider to retrieve the device's last known location or current location.
    // The fused location provider is one of the location APIs in Google Play services.
    // It manages the underlying location technology and provides a simple API so that
    // you can specify requirements at a high level, like high accuracy or low power.
    // It also optimizes the device's use of battery power. We use the simple lastLocation method.
    // https://developer.android.com/develop/sensors-and-location/location/retrieve-current

    //---- get last location
    // We have to create a variable that will hold the last know location state and
    // it will be updated with the getLastLocation function.

    var lastLocation by mutableStateOf<Location?>(null)

    fun getLastLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            app,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = app.getSystemService(
            LOCATION_SERVICE
        ) as LocationManager

        val isGpsEnabled = locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled && !(hasAccessCoarseLocationPermission
                    || hasAccessFineLocationPermission)) {
            lastLocation = null
            return
        }

        // For this to work properly and track changing location
        // use getCurrentLocation and set up a Flow (a bit like LiveData) with a loopback
        // thread that calls trySend to the flow every time the current
        // location is updated. This complicates the code significantly
        // so will omit here. But see:
        // https://medium.com/@alrodiaz15/google-maps-location-jetpack-compose-36cd3fa617a4
        // for a complete example.
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) { // Sets current location to object
                    lastLocation = location
               }
            }
            .addOnFailureListener { exception -> // Logs for debugging
                Log.d(FAA_TAG, "Could not get current location: $exception")
            }
    }
}