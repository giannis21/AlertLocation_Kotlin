package com.example.alertlocation_kotlin.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.database.annotations.NotNull


class ViewPagerAdapter(fa: FragmentActivity?, list: List<Fragment>) : FragmentStateAdapter(fa!!) {
    private var listFragment: List<Fragment> = ArrayList()

    @NotNull
    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getItemCount(): Int {
        return 2
    }

    init {
        listFragment = list
    }
}
