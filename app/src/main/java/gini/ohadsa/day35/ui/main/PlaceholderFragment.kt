package gini.ohadsa.day35.ui.main

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import edu.tomerbu.day35.utils.hasPermission
import gini.ohadsa.day35.R
import gini.ohadsa.day35.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pageViewModel.text.collectLatest {
                    binding.sectionLabel.text = it
                }
            }
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    private var activityListener: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            activityListener = context

            // If fine location permission isn't approved, instructs the parent Activity to replace
            // this fragment with the permission request fragment.
            if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                activityListener?.requestFineLocationPermission()
            }
        } else {
            throw RuntimeException("$context must implement LocationUpdateFragment.Callbacks")
        }
    }


    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }


    interface Callbacks {
        fun requestFineLocationPermission()
        fun requestBackgroundLocationPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}