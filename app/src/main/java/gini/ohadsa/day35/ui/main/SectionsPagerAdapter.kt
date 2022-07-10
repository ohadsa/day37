package gini.ohadsa.day35.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import gini.ohadsa.day35.R



class SectionsPagerAdapter( fm: FragmentActivity)
    : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return PlaceholderFragment.newInstance(position + 1 )
    }
}