/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gini.ohadsa.day35.ui.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import edu.tomerbu.day35.utils.hasPermission
import edu.tomerbu.day35.utils.requestPermissionWithRationale
import gini.ohadsa.day35.BuildConfig
import gini.ohadsa.day35.R
import gini.ohadsa.day35.databinding.FragmentPermissionRequestBinding

private const val TAG = "PermissionRequestFrag"

/**
 * Displays information about why a user should enable either the fine location permission or the
 * background location permission (depending on what is needed).
 *
 * Allows users to grant the permissions as well.
 */
class PermissionRequestFragment : Fragment() {

    // Type of permission to request (fine or background). Set by calling Activity.
    private var permissionRequestType: PermissionRequestType? = null

    private lateinit var binding: FragmentPermissionRequestBinding

    private var activityListener: Callbacks? = null

    // If the user denied a previous permission request, but didn't check "Don't ask again", these
    // Snackbars provided an explanation for why user should approve, i.e., the additional
    // rationale.
    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(
                binding.frameLayout,
                R.string.fine_location_permission_rationale,
                Snackbar.LENGTH_LONG
            )
            .setAction(R.string.ok) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
    }

    private val backgroundRationalSnackbar by lazy {
        Snackbar.make(
                binding.frameLayout,
                R.string.background_location_permission_rationale,
                Snackbar.LENGTH_LONG
            )
            .setAction(R.string.ok) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            activityListener = context
        } else {
            throw RuntimeException("$context must implement PermissionRequestFragment.Callbacks")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequestType =
            arguments?.getSerializable(ARG_PERMISSION_REQUEST_TYPE) as PermissionRequestType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPermissionRequestBinding.inflate(inflater, container, false)

        when (permissionRequestType) {
            PermissionRequestType.FINE_LOCATION -> {

                binding.apply {
                    iconImageView.setImageResource(R.drawable.ic_my_location_24px)

                    titleTextView.text =
                        getString(R.string.fine_location_access_rationale_title_text)

                    detailsTextView.text =
                        getString(R.string.fine_location_access_rationale_details_text)

                    permissionRequestButton.text =
                        getString(R.string.enable_fine_location_button_text)
                }
            }

            PermissionRequestType.BACKGROUND_LOCATION -> {

                binding.apply {
                    iconImageView.setImageResource(R.drawable.ic_my_location_24px)

                    titleTextView.text =
                        getString(R.string.background_location_access_rationale_title_text)

                    detailsTextView.text =
                        getString(R.string.background_location_access_rationale_details_text)

                    permissionRequestButton.text =
                        getString(R.string.enable_background_location_button_text)
                }
            }
            else -> {}
        }

        binding.permissionRequestButton.setOnClickListener {
            when (permissionRequestType) {
                PermissionRequestType.FINE_LOCATION ->
                    requestFineLocationPermission()

                PermissionRequestType.BACKGROUND_LOCATION ->
                    requestBackgroundLocationPermission()
                else -> {}
            }
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive an empty array.
                    Log.d(TAG, "User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    activityListener?.displayLocationUI()

                else -> {

                    val permissionDeniedExplanation =
                        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
                            R.string.fine_permission_denied_explanation
                        } else {
                            R.string.background_permission_denied_explanation
                        }

                    Snackbar.make(
                        binding.frameLayout,
                        permissionDeniedExplanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    private fun requestFineLocationPermission() {
        val permissionApproved =
            context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ?: return

        if (permissionApproved) {
            activityListener?.displayLocationUI()
        } else {
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                fineLocationRationalSnackbar)
        }
    }

    private fun requestBackgroundLocationPermission() {
        val permissionApproved =
            context?.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) ?: return

        if (permissionApproved) {
            activityListener?.displayLocationUI()
        } else {
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE,
                backgroundRationalSnackbar)
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Callbacks {
        fun displayLocationUI()
    }

    companion object {
        private const val ARG_PERMISSION_REQUEST_TYPE =
            "com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.PERMISSION_REQUEST_TYPE"

        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 56

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param permissionRequestType Type of permission you would like to request.
         * @return A new instance of fragment PermissionRequestFragment.
         */
        @JvmStatic
        fun newInstance(permissionRequestType: PermissionRequestType) =
            PermissionRequestFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PERMISSION_REQUEST_TYPE, permissionRequestType)
                }
            }
    }
}

enum class PermissionRequestType {
    FINE_LOCATION, BACKGROUND_LOCATION
}
