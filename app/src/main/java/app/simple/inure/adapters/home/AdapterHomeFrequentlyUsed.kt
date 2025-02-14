package app.simple.inure.adapters.home

import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.simple.inure.R
import app.simple.inure.decorations.overscroll.HorizontalListViewHolder
import app.simple.inure.decorations.ripple.DynamicRippleLinearLayout
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.glide.util.ImageLoader.loadAppIcon
import app.simple.inure.model.PackageStats
import java.util.*

class AdapterHomeFrequentlyUsed(private val list: ArrayList<PackageStats>) : RecyclerView.Adapter<AdapterHomeFrequentlyUsed.Holder>() {

    private var recentlyUpdatedAppsCallbacks: RecentlyUpdatedAppsCallbacks? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_recently_updated, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.icon.transitionName = "frequently_$position"
        holder.icon.loadAppIcon(list[position].packageInfo!!.packageName)
        holder.name.text = list[position].packageInfo!!.applicationInfo.name

        holder.container.setOnClickListener {
            recentlyUpdatedAppsCallbacks?.onRecentAppClicked(list[position].packageInfo!!, holder.icon)
        }

        holder.container.setOnLongClickListener {
            recentlyUpdatedAppsCallbacks?.onRecentAppLongPressed(list[position].packageInfo!!, holder.icon, holder.container)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size / 5
    }

    inner class Holder(itemView: View) : HorizontalListViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.recently_app_icon)
        val name: TypeFaceTextView = itemView.findViewById(R.id.recently_app_name)
        val container: DynamicRippleLinearLayout = itemView.findViewById(R.id.recently_container)
    }

    fun setOnRecentAppsClickedListener(recentlyUpdatedAppsCallbacks: RecentlyUpdatedAppsCallbacks) {
        this.recentlyUpdatedAppsCallbacks = recentlyUpdatedAppsCallbacks
    }

    companion object {
        interface RecentlyUpdatedAppsCallbacks {
            fun onRecentAppClicked(packageInfo: PackageInfo, icon: ImageView)
            fun onRecentAppLongPressed(packageInfo: PackageInfo, icon: ImageView, anchor: ViewGroup)
        }
    }
}