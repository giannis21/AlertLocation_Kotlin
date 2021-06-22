package com.example.alertlocation_kotlin.ui.add_route

import android.app.Dialog
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.alertlocation_kotlin.*
import com.example.alertlocation_kotlin.data.model.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import kotlinx.android.synthetic.main.banner_layout.view.*
import kotlinx.android.synthetic.main.bottomsheet_configuration.*


class ConfigurationBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val fragments: List<Fragment> by lazy {
        listOf(DetailsFragment.newInstance() as Fragment, MapsFragment.newInstance() as Fragment)
    }

    lateinit var tabs: MutableList<Pair<String, Fragment>>
    lateinit var dialog: BottomSheetDialog
    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var motionId: MotionLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onStart() {
        super.onStart()
        bottomsheetOpened=true
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet: FrameLayout =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
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

    override fun getTheme(): Int = R.style.Theme_NoWiredStrapInNavigationBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fromAdapter =  arguments?.getString("fromAdapter")
        requireActivity().window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        viewModel = (activity as MainActivity).viewModel
        viewModel.pointsList.value?.clear()

        initviewpager()

        closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        if (viewModel.addressSelected.value != null)
            saveIcon.visibility=View.GONE


        saveIcon.setOnClickListener {
            if (!missingInfoHandling())
                viewModel.getGroupId(viewModel.usersToSend.value ?: mutableListOf())
        }

        viewModel.groupNotificationKey.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.addRouteToDatabase(
                    Route(
                        0, "#name",
                        viewModel.usersToSend.value ?: mutableListOf(),
                        viewModel.pointsList.value ?: mutableListOf(), viewModel.message, it
                    )
                )
                dialog.dismiss()
            }


        })
    }

    private fun missingInfoHandling(): Boolean {
        if (viewModel.pointsList.value?.isNullOrEmpty()!!) {
            tabLayout.apply {
                try {
                    Handler(Looper.getMainLooper()).postDelayed({
                        selectTab(getTabAt(1))
                    }, 2000)

                    showBanner("You should select at least a point on map!", false)
                    return true
                } catch (e: Exception) {
                }

            }
        }
        if (viewModel.usersToSend.value?.isNullOrEmpty()!!) {
            tabLayout.apply {
                try {
                    Handler(Looper.getMainLooper()).postDelayed({
                        selectTab(getTabAt(0))
                    }, 2000)

                    showBanner("You should add at least a receiver!", false)
                    return true
                } catch (e: Exception) {
                }

            }
        }
        if (viewModel.message.isEmpty()) {
            tabLayout.apply {
                try {
                    Handler(Looper.getMainLooper()).postDelayed({
                        selectTab(getTabAt(0))
                    }, 2000)
                    showBanner("Please type a message first!", false)

                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.transitionToEnd.postValue(true)
                    }, 2000)

                    return true
                } catch (e: Exception) {

                    return true
                }

            }
        }
        return false
    }

    private fun initviewpager() {

        val viewPager: ViewPager2 = view?.findViewById(R.id.view_pager) as ViewPager2
        val tabLayout: TabLayout = view?.findViewById(R.id.tabLayout) as TabLayout
        tabs = mutableListOf(Pair("Details", fragments[0]))

        val viewPagerAdapter = ViewPagerFragmentAdapter(requireActivity(), tabs)
        viewPager.adapter = viewPagerAdapter

        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position].first
        }
        tabLayoutMediator.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.isUserInputEnabled = tab?.position != 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })


        // tabLayoutMediator.detach()
        tabLayout.addTab(tabLayout.newTab().setText("Map"), 1)
        tabs.add(1, Pair("Map", fragments[1]))
        if (viewModel.addressSelected.value != null || viewModel.notificationDeepLink.value==true)
            tabLayout.apply {
                selectTab(getTabAt(1))
            }

        viewPagerAdapter.notifyDataSetChanged()

    }

    class ViewPagerFragmentAdapter(
        fragmentActivity: FragmentActivity,
        var tabs: MutableList<Pair<String, Fragment>>
    ) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int {
            return tabs.size
        }

        override fun createFragment(position: Int): Fragment {
            return tabs[position].second
        }

    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
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
            val insets: Insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomsheetOpened=false
        DetailsFragment.clearValuesListener?.invoke()
        viewModel.groupNotificationKey.postValue(null)
        viewModel.transitionToEnd.postValue(null)
        viewModel.addressSelected.postValue(null)
        viewModel.notificationDeepLink.postValue(false)
        viewModel.notificationBundle.postValue(null)
    }

    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.banner_layout, null)

        activity?.runOnUiThread {
            frameLayout_main?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value

                if(!success){
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.LightRed)
                    cLayout.imageView.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_close_24)

                }else{
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.success_color)
                    cLayout.imageView.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 4000)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConfigurationBottomSheetDialogFragment()
        var bottomsheetOpened=false
    }
}