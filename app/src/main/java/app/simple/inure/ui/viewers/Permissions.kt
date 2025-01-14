package app.simple.inure.ui.viewers

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.adapters.details.AdapterPermissions
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.typeface.TypeFaceEditTextDynamicCorner
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.dialogs.action.PermissionStatusDialog
import app.simple.inure.dialogs.miscellaneous.ErrorPopup
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.extension.popup.PopupMenuCallback
import app.simple.inure.factories.panels.PackageInfoFactory
import app.simple.inure.model.PermissionInfo
import app.simple.inure.popups.viewers.PopupPermissions
import app.simple.inure.preferences.PermissionPreferences
import app.simple.inure.util.ViewUtils.gone
import app.simple.inure.util.ViewUtils.visible
import app.simple.inure.viewmodels.viewers.PermissionsViewModel

class Permissions : ScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var search: DynamicRippleImageButton
    private lateinit var title: TypeFaceTextView
    private lateinit var searchBox: TypeFaceEditTextDynamicCorner
    private lateinit var permissionsViewModel: PermissionsViewModel
    private lateinit var packageInfoFactory: PackageInfoFactory
    private lateinit var adapterPermissions: AdapterPermissions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_permissions, container, false)

        recyclerView = view.findViewById(R.id.permissions_recycler_view)
        search = view.findViewById(R.id.permissions_search_btn)
        searchBox = view.findViewById(R.id.permissions_search)
        title = view.findViewById(R.id.permission_title)
        packageInfo = requireArguments().getParcelable("application_info")!!
        packageInfoFactory = PackageInfoFactory(requireActivity().application, packageInfo)
        permissionsViewModel = ViewModelProvider(this, packageInfoFactory).get(PermissionsViewModel::class.java)

        startPostponedEnterTransition()
        searchBoxState()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionsViewModel.getPermissions().observe(viewLifecycleOwner, {
            adapterPermissions = AdapterPermissions(it, searchBox.text.toString())
            recyclerView.adapter = adapterPermissions

            adapterPermissions.setOnPermissionCallbacksListener(object : AdapterPermissions.Companion.PermissionCallbacks {
                override fun onPermissionClicked(container: View, permissionInfo: PermissionInfo, position: Int) {

                    val popup = PopupPermissions(container, permissionInfo)

                    popup.setOnMenuClickListener(object : PopupMenuCallback {
                        override fun onMenuItemClicked(source: String) {
                            when (source) {
                                getString(R.string.revoke) -> {
                                    val p = PermissionStatusDialog.newInstance(packageInfo, permissionInfo, getString(R.string.revoke))
                                    p.show(childFragmentManager, "permission_status")
                                    p.setOnPermissionStatusCallbackListener(object : PermissionStatusDialog.Companion.PermissionStatusCallbacks {
                                        override fun onSuccess(grantedStatus: Boolean) {
                                            adapterPermissions.permissionStatusChanged(position, grantedStatus)
                                        }
                                    })
                                }
                                getString(R.string.grant) -> {
                                    val p = PermissionStatusDialog.newInstance(packageInfo, permissionInfo, getString(R.string.grant))
                                    p.show(childFragmentManager, "permission_status")
                                    p.setOnPermissionStatusCallbackListener(object : PermissionStatusDialog.Companion.PermissionStatusCallbacks {
                                        override fun onSuccess(grantedStatus: Boolean) {
                                            adapterPermissions.permissionStatusChanged(position, grantedStatus)
                                        }
                                    })
                                }
                            }
                        }
                    })
                }
            })
        })

        permissionsViewModel.getError().observe(viewLifecycleOwner, {
            val e = ErrorPopup.newInstance(it)
            e.show(childFragmentManager, "error_dialog")
            e.setOnErrorDialogCallbackListener(object : ErrorPopup.Companion.ErrorDialogCallbacks {
                override fun onDismiss() {
                    requireActivity().onBackPressed()
                }
            })
        })

        search.setOnClickListener {
            if (searchBox.text.isNullOrEmpty()) {
                PermissionPreferences.setSearchVisibility(!PermissionPreferences.isSearchVisible())
            } else {
                searchBox.text?.clear()
            }
        }

        searchBox.doOnTextChanged { text, _, _, _ ->
            if (searchBox.isFocused) {
                permissionsViewModel.loadPermissionData(text.toString())
            }
        }
    }

    private fun searchBoxState() {
        if (PermissionPreferences.isSearchVisible()) {
            search.setImageResource(R.drawable.ic_close)
            title.gone()
            searchBox.visible(true)
            searchBox.showInput()
        } else {
            search.setImageResource(R.drawable.ic_search)
            title.visible(true)
            searchBox.gone()
            searchBox.hideInput()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PermissionPreferences.permissionSearch -> {
                searchBoxState()
            }
        }
    }

    companion object {
        fun newInstance(applicationInfo: PackageInfo): Permissions {
            val args = Bundle()
            args.putParcelable("application_info", applicationInfo)
            val fragment = Permissions()
            fragment.arguments = args
            return fragment
        }
    }
}