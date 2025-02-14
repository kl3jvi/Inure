package app.simple.inure.decorations.typeface

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import app.simple.inure.R
import app.simple.inure.preferences.AppearancePreferences
import app.simple.inure.util.ColorUtils
import app.simple.inure.util.ColorUtils.resolveAttrColor
import app.simple.inure.util.TypeFace

open class TypeFaceEditText : AppCompatEditText {
    constructor(context: Context?) : super(context!!) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TypeFaceTextView, 0, 0)
        typeface = TypeFace.getTypeFace(AppearancePreferences.getAppFont(), typedArray.getInt(R.styleable.TypeFaceTextView_appFontStyle, 0), context)
        highlightColor = ColorUtils.lightenColor(context.resolveAttrColor(R.attr.colorAppAccentLight), 0.4F)
    }
}
