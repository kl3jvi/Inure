package app.simple.inure.popups.viewers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import app.simple.inure.R
import app.simple.inure.decorations.popup.BasePopupWindow
import app.simple.inure.decorations.popup.PopupLinearLayout
import app.simple.inure.decorations.popup.PopupMenuCallback
import app.simple.inure.decorations.ripple.DynamicRippleTextView

class PopupServicesMenu(view: View, isComponentEnabled: Boolean) : BasePopupWindow() {

    private lateinit var popupMainMenuCallbacks: PopupMenuCallback
    private var componentState: DynamicRippleTextView

    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_services_menu, PopupLinearLayout(view.context))
        val context = contentView.context

        componentState = contentView.findViewById(R.id.popup_component_state_toggle)

        componentState.text = if (isComponentEnabled) {
            context.getString(R.string.disable)
        } else {
            context.getString(R.string.enable)
        }.also {
            componentState.onClick(it)
        }

        init(contentView, view, Gravity.END)

        setOnDismissListener {
            popupMainMenuCallbacks.onDismiss()
        }
    }

    private fun DynamicRippleTextView.onClick(string: String) {
        this.setOnClickListener {
            popupMainMenuCallbacks.onMenuItemClicked(string)
            dismiss()
        }
    }

    fun setOnMenuClickListener(popupMainMenuCallbacks: PopupMenuCallback) {
        this.popupMainMenuCallbacks = popupMainMenuCallbacks
    }
}