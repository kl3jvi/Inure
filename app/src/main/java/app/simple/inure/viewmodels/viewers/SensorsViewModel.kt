package app.simple.inure.viewmodels.viewers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.preferences.SensorsPreferences
import app.simple.inure.preferences.SharedPreferences.getSharedPreferences
import app.simple.inure.util.SensorsSort.getSortedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorsViewModel(application: Application) : AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }

    private val sensors: MutableLiveData<MutableList<Sensor>> by lazy {
        MutableLiveData<MutableList<Sensor>>().also {
            loadSensorData()
        }
    }

    private val error = MutableLiveData<String>()

    fun getSensorsData(): LiveData<MutableList<Sensor>> = sensors
    fun getError(): LiveData<String> = error

    private fun loadSensorData() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                with(getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager) {
                    val list: MutableList<Sensor> = getSensorList(Sensor.TYPE_ALL).toMutableList()

                    list.getSortedList(SensorsPreferences.getSortStyle())

                    this@SensorsViewModel.sensors.postValue(list)
                }
            }.onFailure {
                it.printStackTrace()
                error.postValue(it.stackTraceToString())
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            SensorsPreferences.isSortingReversed,
            SensorsPreferences.sortStyle -> {
                loadSensorData()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }
}