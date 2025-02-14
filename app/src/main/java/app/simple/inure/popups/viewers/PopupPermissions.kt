package app.simple.inure.popups.viewers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.extension.popup.BasePopupWindow
import app.simple.inure.extension.popup.PopupLinearLayout
import app.simple.inure.extension.popup.PopupMenuCallback
import app.simple.inure.model.PermissionInfo

class PopupPermissions(view: View, permissionInfo: PermissionInfo) : BasePopupWindow() {

    private lateinit var popupMainMenuCallbacks: PopupMenuCallback

    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_permission_menu, PopupLinearLayout(view.context))
        val context = contentView.context
        val revoke = contentView.findViewById<DynamicRippleTextView>(R.id.popup_revoke)

        revoke.text = if (permissionInfo.isGranted) {
            context.getString(R.string.revoke)
        } else {
            context.getString(R.string.grant)
        }

        revoke.onClick(revoke.text.toString())

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