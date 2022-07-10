package gini.ohadsa.day35.location

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import gini.ohadsa.day35.R
import gini.ohadsa.day35.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    //flag for state
    private var locationServiceBound = false

    //reference to what the service return in onBind
    private var locationService: LocationService? = null

    //mobitor Connection to the Service
    private val locationServiceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
        }

    }
    override fun onStart() {
        super.onStart()
        //intent... -> bindService(Intent) ** explicit Intent **
        val serviceIntent = Intent(this , LocationService::class.java)
        bindService(serviceIntent, locationServiceConnection , Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if(locationServiceBound) {
            unbindService(locationServiceConnection)
            locationServiceBound = false
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
    }

}