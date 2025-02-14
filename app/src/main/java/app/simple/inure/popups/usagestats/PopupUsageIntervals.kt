package app.simple.inure.popups.usagestats

import android.view.LayoutInflater
import android.view.View
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.extension.popup.BasePopupWindow
import app.simple.inure.extension.popup.PopupLinearLayout
import app.simple.inure.preferences.StatisticsPreferences
import app.simple.inure.util.UsageInterval

class PopupUsageIntervals(view: View) : BasePopupWindow() {
    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_usage_stats_interval, PopupLinearLayout(view.context))
        init(contentView, view)

        contentView.findViewById<DynamicRippleTextView>(R.id.popup_interval_daily).setOnClickListener {
            setInterval(UsageInterval.DAILY)
        }

        contentView.findViewById<DynamicRippleTextView>(R.id.popup_interval_weekly).setOnClickListener {
            setInterval(UsageInterval.WEEKlY)
        }

        contentView.findViewById<DynamicRippleTextView>(R.id.popup_interval_monthly).setOnClickListener {
            setInterval(UsageInterval.MONTHLY)
        }

        contentView.findViewById<DynamicRippleTextView>(R.id.popup_interval_yearly).setOnClickListener {
            setInterval(UsageInterval.YEARLY)
        }
    }

    private fun setInterval(interval: Int) {
        StatisticsPreferences.setInterval(interval).also {
            dismiss()
        }
    }
}