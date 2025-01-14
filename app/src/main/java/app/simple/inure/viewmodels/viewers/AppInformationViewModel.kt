package app.simple.inure.viewmodels.viewers

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.text.Spannable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.R
import app.simple.inure.apk.parsers.APKParser.getApkMeta
import app.simple.inure.apk.parsers.APKParser.getDexData
import app.simple.inure.apk.parsers.APKParser.getGlEsVersion
import app.simple.inure.apk.utils.PackageUtils
import app.simple.inure.apk.utils.PackageUtils.getApplicationInstallTime
import app.simple.inure.apk.utils.PackageUtils.getApplicationLastUpdateTime
import app.simple.inure.extension.viewmodels.WrappedViewModel
import app.simple.inure.preferences.ConfigurationPreferences
import app.simple.inure.util.SDKHelper
import app.simple.inure.util.StringUtils.applyAccentColor
import app.simple.inure.util.StringUtils.applySecondaryTextColor
import com.jaredrummler.apkparser.model.ApkMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat

class AppInformationViewModel(application: Application, val packageInfo: PackageInfo) : WrappedViewModel(application) {

    private val information: MutableLiveData<ArrayList<Pair<String, Spannable>>> by lazy {
        MutableLiveData<ArrayList<Pair<String, Spannable>>>().also {
            viewModelScope.launch(Dispatchers.IO) {
                loadInformation()
            }
        }
    }

    fun getInformation(): LiveData<ArrayList<Pair<String, Spannable>>> {
        return information
    }

    private fun loadInformation() {
        information.postValue(arrayListOf(
            getPackageName(),
            getVersion(),
            getVersionCode(),
            getInstallLocation(),
            getGlesVersion(),
            getUID(),
            getInstallDate(),
            getUpdateDate(),
            getMinSDK(),
            getTargetSDK(),
            getMethodCount(),
            getApex(),
            getApplicationType(),
            getInstallerName()
        ))
    }

    private fun getPackageName(): Pair<String, Spannable> {
        return Pair(getString(R.string.package_name),
                    packageInfo.packageName.applySecondaryTextColor(context))
    }

    private fun getVersion(): Pair<String, Spannable> {
        return Pair(getString(R.string.version),
                    PackageUtils.getApplicationVersion(context, packageInfo).applySecondaryTextColor(context))
    }

    private fun getVersionCode(): Pair<String, Spannable> {
        return Pair(getString(R.string.version),
                    PackageUtils.getApplicationVersionCode(context, packageInfo).applySecondaryTextColor(context))
    }

    private fun getInstallLocation(): Pair<String, Spannable> {
        val installLocation = kotlin.runCatching {
            when (packageInfo.installLocation) {
                PackageInfo.INSTALL_LOCATION_AUTO -> getString(R.string.auto)
                PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY -> getString(R.string.internal)
                PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL -> getString(R.string.prefer_external)
                else -> {
                    if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                        getString(R.string.system)
                    } else {
                        getString(R.string.not_available)
                    }
                }
            }
        }.getOrElse {
            getString(R.string.not_available)
        }

        return Pair(getString(R.string.install_location),
                    installLocation.applySecondaryTextColor(context))
    }

    private fun getGlesVersion(): Pair<String, Spannable> {
        val glesVersion = kotlin.runCatching {
            if (packageInfo.getGlEsVersion().isEmpty()) {
                getString(R.string.not_available)
            } else {
                packageInfo.getGlEsVersion()
            }
        }.getOrElse {
            getString(R.string.not_available)
        }

        return Pair(getString(R.string.gles_version),
                    glesVersion.applySecondaryTextColor(context))
    }

    private fun getUID(): Pair<String, Spannable> {
        return Pair(getString(R.string.uid),
                    packageInfo.applicationInfo.uid.toString().applySecondaryTextColor(context))
    }

    private fun getInstallDate(): Pair<String, Spannable> {
        return Pair(getString(R.string.install_date),
                    packageInfo.getApplicationInstallTime(context, ConfigurationPreferences.getDateFormat()).applyAccentColor())
    }

    private fun getUpdateDate(): Pair<String, Spannable> {
        return Pair(getString(R.string.update_date),
                    packageInfo.getApplicationLastUpdateTime(context, ConfigurationPreferences.getDateFormat()).applyAccentColor())
    }

    private fun getMinSDK(): Pair<String, Spannable> {
        val minSdk = kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                "${packageInfo.applicationInfo.minSdkVersion}, ${SDKHelper.getSdkTitle(packageInfo.applicationInfo.minSdkVersion)}"
            } else {
                when (val apkMeta: Any? = packageInfo.applicationInfo.getApkMeta()) {
                    is ApkMeta -> {
                        "${apkMeta.minSdkVersion}, ${SDKHelper.getSdkTitle(apkMeta.minSdkVersion)}"
                    }
                    is net.dongliu.apk.parser.bean.ApkMeta -> {
                        "${apkMeta.minSdkVersion}, ${SDKHelper.getSdkTitle(apkMeta.minSdkVersion)}"
                    }
                    else -> {
                        getString(R.string.not_available)
                    }
                }
            }
        }.getOrElse {
            getString(R.string.not_available)
        }

        return Pair(getString(R.string.minimum_sdk),
                    minSdk.applyAccentColor())
    }

    private fun getTargetSDK(): Pair<String, Spannable> {
        val targetSdk = kotlin.runCatching {
            "${packageInfo.applicationInfo.targetSdkVersion}, ${SDKHelper.getSdkTitle(packageInfo.applicationInfo.targetSdkVersion)}"
        }.getOrElse {
            it.message!!
        }

        return Pair(getString(R.string.target_sdk),
                    targetSdk.applyAccentColor())
    }

    private fun getMethodCount(): Pair<String, Spannable> {
        val method = kotlin.runCatching {
            val p0 = packageInfo.applicationInfo.getDexData()!!
            var count = 0

            for (i in p0) {
                count = i.header.methodIdsSize
            }

            if (p0.size > 1) {
                String.format(getString(R.string.multi_dex), NumberFormat.getNumberInstance().format(count))
            } else {
                String.format(getString(R.string.single_dex), NumberFormat.getNumberInstance().format(count))
            }
        }.getOrElse {
            it.message!!
        }

        return Pair(getString(R.string.method_count),
                    method.applySecondaryTextColor(context))
    }

    private fun getApex(): Pair<String, Spannable> {
        val apex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (packageInfo.isApex)
                getString(R.string.yes) else getString(R.string.no)
        } else {
            getString(R.string.not_available)
        }

        return Pair(getString(R.string.apex),
                    apex.applySecondaryTextColor(context))
    }

    private fun getApplicationType(): Pair<String, Spannable> {
        val applicationType = if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            getString(R.string.system)
        } else {
            getString(R.string.user)
        }

        return Pair(getString(R.string.application_type),
                    applicationType.applySecondaryTextColor(context))
    }

    private fun getInstallerName(): Pair<String, Spannable> {
        @Suppress("deprecation")
        val name = kotlin.runCatching {
            val p0 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getApplication<Application>().packageManager.getInstallSourceInfo(packageInfo.packageName).installingPackageName
            } else {
                getApplication<Application>().packageManager.getInstallerPackageName(packageInfo.packageName)
            }

            PackageUtils.getApplicationName(context, p0!!)
        }.getOrElse {
            getString(R.string.not_available)
        }

        return Pair(getString(R.string.installer),
                    name!!.applySecondaryTextColor(context))
    }
}
