package app.simple.inure.ui.viewers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.decorations.views.CustomWebView
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.factories.panels.XmlDataFactory
import app.simple.inure.popups.app.PopupXmlViewer
import app.simple.inure.util.ColorUtils.resolveAttrColor
import app.simple.inure.util.NullSafety.isNull
import app.simple.inure.util.ViewUtils.gone
import app.simple.inure.viewmodels.viewers.XMLViewerData
import java.io.IOException


class XMLViewerWebView : ScopedFragment() {

    private lateinit var manifest: CustomWebView
    private lateinit var name: TypeFaceTextView
    private lateinit var options: DynamicRippleImageButton
    private lateinit var progress: ProgressBar

    private var code = ""

    private lateinit var componentsViewModel: XMLViewerData
    private lateinit var applicationInfoFactory: XmlDataFactory

    private val exportManifest = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
        if (uri == null) {
            // Back button pressed.
            return@registerForActivityResult
        }
        try {
            requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream == null) throw IOException()
                outputStream.write(code.toByteArray())
                outputStream.flush()
                Toast.makeText(requireContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), R.string.failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_web_viewer, container, false)

        manifest = view.findViewById(R.id.source_view)
        name = view.findViewById(R.id.xml_name)
        options = view.findViewById(R.id.xml_viewer_options)
        progress = view.findViewById(R.id.xml_loader)

        manifest.enableWithWebClient()

        packageInfo = requireArguments().getParcelable("application_info")!!

        applicationInfoFactory = XmlDataFactory(packageInfo, requireArguments().getBoolean("is_manifest"),
                                                requireArguments().getString("path_to_xml")!!,
                                                requireActivity().application,
                                                requireContext().resolveAttrColor(R.attr.colorAppAccent))

        componentsViewModel = ViewModelProvider(this, applicationInfoFactory).get(XMLViewerData::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPostponedEnterTransition()

        componentsViewModel.getString().observe(viewLifecycleOwner, {
            if (savedInstanceState.isNull()) {
                manifest.loadDataWithBaseURL("file:///android_asset/", it, "text/html", "UTF-8", null)
            } else {
                manifest.restoreState(savedInstanceState!!)
            }
            this@XMLViewerWebView.name.text = requireArguments().getString("path_to_xml")!!
            progress.gone()
            code = it
        })

        options.setOnClickListener {
            val p = PopupXmlViewer(it)

            p.setOnPopupClickedListener(object : PopupXmlViewer.PopupXmlCallbacks {
                override fun onPopupItemClicked(source: String) {
                    when (source) {
                        getString(R.string.copy) -> {
                            val clipboard: ClipboardManager? = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                            val clip = ClipData.newPlainText("xml", code)
                            clipboard?.setPrimaryClip(clip)
                        }
                        getString(R.string.save) -> {
                            val fileName: String = packageInfo.packageName + "_" + name.text
                            exportManifest.launch(fileName)
                        }
                    }
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        manifest.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    companion object {
        fun newInstance(applicationInfo: PackageInfo, isManifest: Boolean, pathToXml: String?): XMLViewerWebView {
            val args = Bundle()
            args.putParcelable("application_info", applicationInfo)
            args.putBoolean("is_manifest", isManifest)
            args.putString("path_to_xml", pathToXml)
            val fragment = XMLViewerWebView()
            fragment.arguments = args
            return fragment
        }
    }
}
