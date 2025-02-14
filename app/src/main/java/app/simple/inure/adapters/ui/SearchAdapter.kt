package app.simple.inure.adapters.ui

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.simple.inure.R
import app.simple.inure.decorations.overscroll.VerticalListViewHolder
import app.simple.inure.glide.modules.GlideApp
import app.simple.inure.glide.util.ImageLoader.loadAppIcon
import app.simple.inure.interfaces.adapters.AppsAdapterCallbacks
import app.simple.inure.util.AdapterUtils
import app.simple.inure.util.FileSizeHelper.toSize

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.Holder>() {

    var apps = arrayListOf<PackageInfo>()
    var searchKeyword = ""
    private lateinit var appsAdapterCallbacks: AppsAdapterCallbacks
    private var xOff = 0f
    private var yOff = 0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context)
                              .inflate(R.layout.adapter_all_apps_small_details, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.icon.transitionName = "app_$position"
        holder.icon.loadAppIcon(apps[position].packageName)
        holder.name.text = apps[position].applicationInfo.name
        holder.packageId.text = apps[position].packageName

        holder.packageType.text = if ((apps[position].applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
            holder.packageType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0)
            holder.itemView.context.getString(R.string.user)
        } else {
            holder.packageType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android, 0, 0, 0)
            holder.itemView.context.getString(R.string.system)
        }

        holder.packageSize.text = apps[position].applicationInfo.sourceDir.toSize()

        holder.container.setOnClickListener {
            appsAdapterCallbacks.onAppClicked(apps[position], holder.icon)
        }

        if (searchKeyword.isNotEmpty()) {
            AdapterUtils.searchHighlighter(holder.name, searchKeyword)
            AdapterUtils.searchHighlighter(holder.packageId, searchKeyword)
        }

        holder.container.setOnLongClickListener {
            appsAdapterCallbacks.onAppLongPress(apps[position], it, holder.icon, position)
            true
        }
    }

    override fun onViewRecycled(holder: Holder) {
        super.onViewRecycled(holder)
        GlideApp.with(holder.icon).clear(holder.icon)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun setOnItemClickListener(appsAdapterCallbacks: AppsAdapterCallbacks) {
        this.appsAdapterCallbacks = appsAdapterCallbacks
    }

    inner class Holder(itemView: View) : VerticalListViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.adapter_all_app_icon)
        val name: TextView = itemView.findViewById(R.id.adapter_all_app_name)
        val packageId: TextView = itemView.findViewById(R.id.adapter_recently_app_package_id)
        val packageSize: TextView = itemView.findViewById(R.id.adapter_all_app_package_size)
        val packageType: TextView = itemView.findViewById(R.id.adapter_all_app_type)
        val container: ConstraintLayout = itemView.findViewById(R.id.adapter_all_app_container)
    }
}
