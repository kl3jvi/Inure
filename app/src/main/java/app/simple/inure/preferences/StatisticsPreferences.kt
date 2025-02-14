package app.simple.inure.preferences

import app.simple.inure.popups.apps.PopupAppsCategory
import app.simple.inure.preferences.SharedPreferences.getSharedPreferences
import app.simple.inure.util.SortUsageStats
import app.simple.inure.util.UsageInterval
import org.jetbrains.annotations.NotNull

object StatisticsPreferences {
    const val appsCategory = "stats_app_category"
    const val statsInterval = "usage_stats_intervals"
    const val statsSorting = "stats_sorted_by"
    const val isSortingReversed = "stats_is_sorting_reversed"

    // ---------------------------------------------------------------------------------------------------------- //

    fun setInterval(value: Int) {
        getSharedPreferences().edit().putInt(statsInterval, value).apply()
    }

    fun getInterval(): Int {
        return getSharedPreferences().getInt(statsInterval, UsageInterval.WEEKlY)
    }

    // ---------------------------------------------------------------------------------------------------------- //

    fun setSortType(value: String) {
        getSharedPreferences().edit().putString(statsSorting, value).apply()
    }

    fun getSortedBy(): String {
        return getSharedPreferences().getString(statsSorting, SortUsageStats.TIME)!!
    }

    // ---------------------------------------------------------------------------------------------------------- //

    fun setReverseSorting(@NotNull value: Boolean) {
        getSharedPreferences().edit().putBoolean(isSortingReversed, value).apply()
    }

    fun isReverseSorting(): Boolean {
        return getSharedPreferences().getBoolean(isSortingReversed, true)
    }

    // ---------------------------------------------------------------------------------------------------------- //

    fun setAppsCategory(value: String) {
        getSharedPreferences().edit().putString(appsCategory, value).apply()
    }

    fun getAppsCategory(): String {
        return getSharedPreferences().getString(appsCategory, PopupAppsCategory.BOTH)!!
    }
}
