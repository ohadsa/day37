package gini.ohadsa.day35

import android.os.Bundle

import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import gini.ohadsa.day35.ui.main.SectionsPagerAdapter
import gini.ohadsa.day35.databinding.ActivityMainBinding
import gini.ohadsa.day35.ui.main.PlaceholderFragment
import gini.ohadsa.day35.ui.permission.PermissionRequestFragment
import gini.ohadsa.day35.ui.permission.PermissionRequestType

class MainActivity : AppCompatActivity(), PermissionRequestFragment.Callbacks,
    PlaceholderFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlaceholderFragment.newInstance(1)).commit()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }

    // Triggers a splash screen (fragment) to help users decide if they want to approve the missing
    // fine location permission.
    override fun requestFineLocationPermission() {
        val fragment = PermissionRequestFragment.newInstance(PermissionRequestType.FINE_LOCATION)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Triggers a splash screen (fragment) to help users decide if they want to approve the missing
    // background location permission.
    override fun requestBackgroundLocationPermission() {
        val fragment = PermissionRequestFragment.newInstance(
            PermissionRequestType.BACKGROUND_LOCATION
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun displayLocationUI() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlaceholderFragment.newInstance(1)).commit()


    }
}