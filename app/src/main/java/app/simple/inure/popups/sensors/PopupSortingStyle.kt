package app.simple.inure.popups.sensors

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.decorations.views.CustomCheckBox
import app.simple.inure.extension.popup.BasePopupWindow
import app.simple.inure.extension.popup.PopupLinearLayout
import app.simple.inure.preferences.SensorsPreferences
import app.simple.inure.util.SensorsSort

class PopupSortingStyle(view: View) : BasePopupWindow() {

    private val name: DynamicRippleTextView
    private val power: DynamicRippleTextView
    private val maximumRange: DynamicRippleTextView
    private val resolution: DynamicRippleTextView

    private val descCheckBox: CustomCheckBox

    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_sensors_sort, PopupLinearLayout(view.context))

        name = contentView.findViewById(R.id.sort_name)
        power = contentView.findViewById(R.id.sort_power)
        maximumRange = contentView.findViewById(R.id.sort_max_range)
        resolution = contentView.findViewById(R.id.sort_res)
        descCheckBox = contentView.findViewById(R.id.sort_reversed_checkbox)

        when (SensorsPreferences.getSortStyle()) {
            SensorsSort.NAME -> name.isSelected = true
            SensorsSort.POWER -> power.isSelected = true
            SensorsSort.MAX_RANGE -> maximumRange.isSelected = true
            SensorsSort.RESOLUTION -> resolution.isSelected = true
        }

        descCheckBox.isChecked = SensorsPreferences.isReverseSorting()

        name.onClick(SensorsSort.NAME)
        power.onClick(SensorsSort.POWER)
        maximumRange.onClick(SensorsSort.MAX_RANGE)
        resolution.onClick(SensorsSort.RESOLUTION)

        descCheckBox.setOnCheckedChangeListener { _, isChecked ->
            SensorsPreferences.setReverseSorting(isChecked)
        }

        init(contentView, view)
    }

    private fun TextView.onClick(style: String) {
        this.setOnClickListener {
            SensorsPreferences.setSortStyle(style)
            dismiss()
        }
    }
}