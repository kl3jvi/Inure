package app.simple.inure.ui.viewers

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import app.simple.inure.R
import app.simple.inure.adapters.home.AdapterRecentlyUpdated
import app.simple.inure.apk.utils.PackageUtils.launchThisPackage
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.dialogs.action.Preparing
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.extension.popup.PopupMenuCallback
import app.simple.inure.interfaces.adapters.AppsAdapterCallbacks
import app.simple.inure.popups.app.PopupMainList
import app.simple.inure.ui.app.AppInfo
import app.simple.inure.util.FragmentHelper
import app.simple.inure.viewmodels.panels.HomeViewModel

class RecentlyUpdated : ScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var back: DynamicRippleImageButton
    private lateinit var adapterRecentlyUpdated: AdapterRecentlyUpdated

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recently_updated, container, false)

        recyclerView = view.findViewById(R.id.recently_updated_recycler_view)
        back = view.findViewById(R.id.recently_updated_back_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getUpdatedApps().observe(viewLifecycleOwner, {
            postponeEnterTransition()

            adapterRecentlyUpdated = AdapterRecentlyUpdated()
            adapterRecentlyUpdated.apps = it
            recyclerView.adapter = adapterRecentlyUpdated

            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }

            adapterRecentlyUpdated.setOnItemClickListener(object : AppsAdapterCallbacks {
                override fun onAppClicked(packageInfo: PackageInfo, icon: ImageView) {
                    openAppInfo(packageInfo, icon)
                }

                override fun onAppLongPress(packageInfo: PackageInfo, anchor: View, icon: ImageView, position: Int) {
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
            })

            back.setOnClickListener {
                requireActivity().onBackPressed()
            }
        })
    }

    private fun openAppInfo(packageInfo: PackageInfo, icon: ImageView) {
        FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                    AppInfo.newInstance(packageInfo, icon.transitionName),
                                    icon, "app_info")
    }

    companion object {
        fun newInstance(): RecentlyUpdated {
            val args = Bundle()
            val fragment = RecentlyUpdated()
            fragment.arguments = args
            return fragment
        }
    }
}