package com.example.alertlocation_kotlin.ui.add_route

import android.app.Dialog
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.bottomsheet_configuration.*


class ConfigurationBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val fragments : List<Fragment> by lazy {
        listOf(DetailsFragment.newInstance() as Fragment, MapsFragment.newInstance() as Fragment)
    }

    lateinit var tabs: MutableList<Pair<String, Fragment>>
    lateinit var dialog:BottomSheetDialog
    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            behavior.skipCollapsed = true
            setupFullHeight(dialog)
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_configuration, container, false);
    }

    override fun getTheme(): Int  = R.style.Theme_NoWiredStrapInNavigationBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)


        val routeDao = RouteRoomDatabase.getDatabase(requireContext()).routeDao()
        viewModelFactory = ViewmodelFactory(mainRepository(routeDao), requireContext())

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)
        viewModel.pointsList.value?.clear()
        initviewpager()

        closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        saveIcon.setOnClickListener {
           viewModel.addRouteToDatabase(
               Route(0,"route ${System.currentTimeMillis()}",
                   viewModel.usersToSend.value ?: mutableListOf(),
                   viewModel.pointsList.value ?: mutableListOf(),viewModel.message))
            dialog.dismiss()
        }
    }
    private fun initviewpager() {

        val viewPager: ViewPager2 = view?.findViewById(R.id.view_pager) as ViewPager2
        val tabLayout: TabLayout = view?.findViewById(R.id.tabLayout) as TabLayout
        tabs = mutableListOf(Pair("Details", fragments[0]))

        val viewPagerAdapter = ViewPagerFragmentAdapter(requireActivity(),tabs)
        viewPager.adapter = viewPagerAdapter

        val tabLayoutMediator=TabLayoutMediator(tabLayout,viewPager){tab, position ->
            tab.text = tabs[position].first
        }
        tabLayoutMediator.attach()

        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.isUserInputEnabled = tab?.position != 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })


       // tabLayoutMediator.detach()
        tabLayout.addTab(tabLayout.newTab().setText("Map"), 1)
        tabs.add(1, Pair("Map", fragments[1]))
        viewPagerAdapter.notifyDataSetChanged()

        val tabs = tabLayout.getChildAt(0) as ViewGroup

//        for (i in 0 until tabs.childCount ) {
//            val tab = tabs.getChildAt(i)
//            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
//            layoutParams.weight = 0f
//            layoutParams.marginEnd = 12
//            layoutParams.marginStart = 12
//
//            tab.layoutParams = layoutParams
//            tabLayout.requestLayout()
//        }
    }

    class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity, var tabs: MutableList<Pair<String, Fragment>>) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return tabs.size
        }

        override fun createFragment(position: Int): Fragment {
            return  tabs[position].second
        }

    }
    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DetailsFragment.clearValuesListener?.invoke()
    }


    companion object{
        @JvmStatic
        fun newInstance() = ConfigurationBottomSheetDialogFragment()
    }
}