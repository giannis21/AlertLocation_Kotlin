package com.example.alertlocation_kotlin.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.database.annotations.NotNull


//class ViewPagerAdapter(manager: FragmentManager) : FragmentStateAdapter(
//    manager,
//    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
//) {
//    override fun getItem(position: Int): Fragment  {
//
//        return when (position) {
//            0 -> DetailsFragment()
//            1 -> MapsFragment()
//            else -> DetailsFragment()
//        }
//    }
//
//    // this counts total number of tabs
//    override fun getCount(): Int {
//        return 2
//    }
//}
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
