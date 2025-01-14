package app.simple.inure.ui.subviewers

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.adapters.details.AdapterInformation
import app.simple.inure.constants.BundleConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.factories.subpanels.ActivityInfoFactory
import app.simple.inure.model.ActivityInfoModel
import app.simple.inure.viewmodels.subviewers.ActivityInfoViewModel

class ActivityInfo : ScopedFragment() {

    private lateinit var name: TypeFaceTextView
    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var backButton: DynamicRippleImageButton
    private lateinit var activityInfoViewModel: ActivityInfoViewModel
    private lateinit var activityInfoFactory: ActivityInfoFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_activity_details, container, false)

        name = view.findViewById(R.id.activity_name)
        recyclerView = view.findViewById(R.id.activity_info_recycler_view)
        backButton = view.findViewById(R.id.activity_info_back_button)

        with(requireArguments().getParcelable<ActivityInfoModel>(BundleConstants.activityInfo)!!) {
            packageInfo = requireArguments().getParcelable(BundleConstants.packageInfo)!!
            this@ActivityInfo.name.text = name
            activityInfoFactory = ActivityInfoFactory(requireActivity().application, this, packageInfo)
        }

        activityInfoViewModel = ViewModelProvider(this, activityInfoFactory).get(ActivityInfoViewModel::class.java)

        startPostponedEnterTransition()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityInfoViewModel.getActivityInfo().observe(viewLifecycleOwner, {
            recyclerView.adapter = AdapterInformation(it)
        })

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    companion object {
        fun newInstance(activityInfoModel: ActivityInfoModel, packageInfo: PackageInfo): ActivityInfo {
            val args = Bundle()
            args.putParcelable(BundleConstants.activityInfo, activityInfoModel)
            args.putParcelable(BundleConstants.packageInfo, packageInfo)
            val fragment = ActivityInfo()
            fragment.arguments = args
            return fragment
        }
    }
}