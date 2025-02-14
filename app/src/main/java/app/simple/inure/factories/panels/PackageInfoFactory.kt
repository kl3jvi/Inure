package app.simple.inure.factories.panels

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.viewmodels.dialogs.FilePreparingViewModel
import app.simple.inure.viewmodels.panels.InfoPanelMenuData
import app.simple.inure.viewmodels.viewers.*

class PackageInfoFactory(private val application: Application, private val packageInfo: PackageInfo)
    : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") // Cast is checked
        when {
            modelClass.isAssignableFrom(FilePreparingViewModel::class.java) -> {
                return FilePreparingViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ApkDataViewModel::class.java) -> {
                return ApkDataViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(InfoPanelMenuData::class.java) -> {
                return InfoPanelMenuData(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(AppInformationViewModel::class.java) -> {
                return AppInformationViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(CertificatesViewModel::class.java) -> {
                return CertificatesViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(DexDataViewModel::class.java) -> {
                return DexDataViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ActivitiesViewModel::class.java) -> {
                return ActivitiesViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ExtrasViewModel::class.java) -> {
                return ExtrasViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(GraphicsViewModel::class.java) -> {
                return GraphicsViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(PermissionsViewModel::class.java) -> {
                return PermissionsViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ServicesViewModel::class.java) -> {
                return ServicesViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ProvidersViewModel::class.java) -> {
                return ProvidersViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(ReceiversViewModel::class.java) -> {
                return ReceiversViewModel(application, packageInfo) as T
            }
            modelClass.isAssignableFrom(SharedLibraryViewModel::class.java) -> {
                return SharedLibraryViewModel(application, packageInfo) as T
            }
            else -> {
                /**
                 * This viewmodel factory is specific to
                 * [FilePreparingViewModel] and assigning it properly
                 * won't throw this exception
                 */
                throw IllegalArgumentException("Nope!!, Wrong Viewmodel!!")
            }
        }
    }
}
