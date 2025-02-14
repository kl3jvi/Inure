package app.simple.inure.viewmodels.viewers

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.constants.Misc
import app.simple.inure.model.PermissionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class PermissionsViewModel(application: Application, val packageInfo: PackageInfo) : AndroidViewModel(application) {

    private val permissions: MutableLiveData<MutableList<PermissionInfo>> by lazy {
        MutableLiveData<MutableList<PermissionInfo>>().also {
            loadPermissionData("")
        }
    }

    private val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getPermissions(): LiveData<MutableList<PermissionInfo>> {
        return permissions
    }

    fun getError(): LiveData<String> {
        return error
    }

    fun loadPermissionData(keyword: String) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                val appPackageInfo = getApplication<Application>().packageManager.getPackageInfo(packageInfo.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = arrayListOf<PermissionInfo>()

                for (count in appPackageInfo.requestedPermissions.indices) {
                    if (appPackageInfo.requestedPermissionsFlags[count] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                        if (appPackageInfo.requestedPermissions[count].lowercase().contains(keyword.lowercase())) {
                            permissions.add(PermissionInfo(true, appPackageInfo.requestedPermissions[count]))
                        }
                    } else {
                        if (appPackageInfo.requestedPermissions[count].lowercase().contains(keyword.lowercase())) {
                            permissions.add(PermissionInfo(false, appPackageInfo.requestedPermissions[count]))
                        }
                    }
                }

                this@PermissionsViewModel.permissions.postValue(permissions.apply {
                    sortBy {
                        it.name.lowercase(Locale.getDefault())
                    }
                })
            }.getOrElse {
                delay(Misc.delay)
                error.postValue(it.stackTraceToString())
            }
        }
    }
}