package app.simple.inure.ui.panels

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.adapters.ui.StatisticsAdapter
import app.simple.inure.apk.utils.PackageUtils.launchThisPackage
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.dialogs.action.Preparing
import app.simple.inure.dialogs.usagestats.UsageStatsMenu
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.extension.popup.PopupMenuCallback
import app.simple.inure.popups.app.PopupMainList
import app.simple.inure.popups.usagestats.PopupAppsCategory
import app.simple.inure.popups.usagestats.PopupUsageStatsSorting
import app.simple.inure.preferences.StatisticsPreferences
import app.simple.inure.ui.app.AppInfo
import app.simple.inure.ui.viewers.Information
import app.simple.inure.util.FragmentHelper
import app.simple.inure.viewmodels.panels.UsageStatsData

class Statistics : ScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var statisticsAdapter: StatisticsAdapter
    private lateinit var usageStatsData: UsageStatsData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        recyclerView = view.findViewById(R.id.usage_rv)

        usageStatsData = ViewModelProvider(requireActivity())[UsageStatsData::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usageStatsData.usageData.observe(viewLifecycleOwner, {
            postponeEnterTransition()

            statisticsAdapter = StatisticsAdapter(it)

            statisticsAdapter.setOnStatsCallbackListener(object : StatisticsAdapter.Companion.StatsAdapterCallbacks {
                override fun onAppClicked(packageInfo: PackageInfo, icon: ImageView) {
                    openAppInfo(packageInfo, icon)
                }

                override fun onAppLongClicked(packageInfo: PackageInfo, icon: ImageView, anchor: ViewGroup) {
                    PopupMainList(anchor, packageInfo.packageName).setOnMenuItemClickListener(object : PopupMenuCallback {
                        override fun onMenuItemClicked(source: String) {
                            when (source) {
                                getString(R.string.launch) -> {
                                    packageInfo.launchThisPackage(requireContext())
                                }
                                getString(R.string.app_information) -> {
                                    FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                                                Information.newInstance(packageInfo),
                                                                "information")
                                }
                                getString(R.string.send) -> {
                                    Preparing.newInstance(packageInfo)
                                        .show(parentFragmentManager, "send_app")
                                }
                            }
                        }
                    })
                }

                override fun onSortPressed(view: View) {
                    PopupUsageStatsSorting(view)
                }

                override fun onFilterPressed(view: View) {
                    PopupAppsCategory(view)
                }

                override fun onSettingsPressed(view: View) {
                    UsageStatsMenu.newInstance()
                        .show(childFragmentManager, "menu")
                }
            })

            recyclerView.adapter = statisticsAdapter

            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        })
    }

    private fun openAppInfo(applicationInfo: PackageInfo, icon: ImageView) {
        FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                    AppInfo.newInstance(applicationInfo, icon.transitionName),
                                    icon, "app_info")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            StatisticsPreferences.statsInterval,
            StatisticsPreferences.appsCategory -> {
                usageStatsData.loadAppStats()
            }
            StatisticsPreferences.isSortingReversed,
            StatisticsPreferences.statsSorting -> {
                usageStatsData.sortUsageData()
            }
        }
    }

    companion object {
        fun newInstance(): Statistics {
            val args = Bundle()
            val fragment = Statistics()
            fragment.arguments = args
            return fragment
        }
    }
}
