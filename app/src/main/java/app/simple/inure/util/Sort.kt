package app.simple.inure.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import app.simple.inure.preferences.MainPreferences
import app.simple.inure.util.FileSizeHelper.getDirectoryLength
import java.util.*

object Sort {

    /**
     * Sorts the [PackageInfo] [ArrayList] by
     * [ApplicationInfo.name]
     */
    const val NAME = "name"

    /**
     * Sorts the [PackageInfo] [ArrayList] by
     * [PackageInfo.packageName]
     */
    const val PACKAGE_NAME = "package_name"

    /**
     * Sorts the [PackageInfo] [ArrayList] by
     * apps directory size
     */
    const val SIZE = "size"

    /**
     * Sorts the [PackageInfo] [ArrayList] by
     * apps install date
     */
    const val INSTALL_DATE = "install_date"

    /**
     * Sort the given [ArrayList] in various ways
     *
     * @param type use [Sort.NAME] constants
     *             to specify sorting methods for the list
     *
     * @throws IllegalArgumentException if the [type] parameter
     *                                  is specified correctly
     */
    fun ArrayList<PackageInfo>.getSortedList(type: String, context: Context) {
        when (type) {
            NAME -> {
                this.sortByName()
            }
            PACKAGE_NAME -> {
                this.sortByPackageName()
            }
            SIZE -> {
                this.sortBySize()
            }
            INSTALL_DATE -> {
                this.sortByInstallDate()
            }
            else -> {
                throw IllegalArgumentException("use default sorting constants to sort the list")
            }
        }
    }

    /**
     * sort application list name
     */
    private fun ArrayList<PackageInfo>.sortByName() {
        return if (MainPreferences.isReverseSorting()) {
            this.sortByDescending {
                it.applicationInfo.name.lowercase(Locale.getDefault())
            }
        } else {
            this.sortBy {
                it.applicationInfo.name.lowercase(Locale.getDefault())
            }
        }
    }

    /**
     * sort application list package name
     */
    private fun ArrayList<PackageInfo>.sortBySize() {
        return if (MainPreferences.isReverseSorting()) {
            this.sortByDescending {
                it.applicationInfo.sourceDir.getDirectoryLength()
            }
        } else {
            this.sortBy {
                it.applicationInfo.sourceDir.getDirectoryLength()
            }
        }
    }

    /**
     * sort application list size
     */
    private fun ArrayList<PackageInfo>.sortByPackageName() {
        return if (MainPreferences.isReverseSorting()) {
            this.sortByDescending {
                it.packageName.lowercase(Locale.getDefault())
            }
        } else {
            this.sortBy {
                it.packageName.lowercase(Locale.getDefault())
            }
        }
    }

    /**
     * sort application list alphabetically
     */
    private fun ArrayList<PackageInfo>.sortByInstallDate() {
        return if (MainPreferences.isReverseSorting()) {
            this.sortByDescending {
                it.firstInstallTime
            }
        } else {
            this.sortBy {
                it.firstInstallTime
            }
        }
    }
}