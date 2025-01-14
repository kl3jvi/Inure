package app.simple.inure.activities.association

import android.graphics.Typeface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import app.simple.inure.R
import app.simple.inure.constants.Quotes
import app.simple.inure.dialogs.miscellaneous.ErrorPopup
import app.simple.inure.extension.activities.BaseActivity
import app.simple.inure.util.TTFHelper
import app.simple.inure.util.TextViewUtils.toHtmlSpanned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TTFAssociationActivity : BaseActivity() {

    private lateinit var fontEditText: EditText
    private lateinit var fontName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_font_viewer)

        fontEditText = findViewById(R.id.ttf_viewer)
        fontName = findViewById(R.id.ttf_name)

        fontName.text = kotlin.runCatching {
            DocumentFile.fromSingleUri(this, intent!!.data!!)!!.name
        }.getOrElse {
            getString(R.string.not_available)
        }

        lifecycleScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                val typeFace = TTFHelper.getTTFFile(contentResolver.openInputStream(intent.data!!)!!, applicationContext)

                withContext(Dispatchers.Main) {
                    fontEditText.setTypeface(typeFace, Typeface.NORMAL)
                    fontName.setTypeface(typeFace, Typeface.NORMAL)
                    fontEditText.setText(Quotes.quotes.random().toHtmlSpanned())
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    val e = ErrorPopup.newInstance(it.stackTraceToString())
                    e.show(supportFragmentManager, "error_dialog")
                    e.setOnErrorDialogCallbackListener(object : ErrorPopup.Companion.ErrorDialogCallbacks {
                        override fun onDismiss() {
                            onBackPressed()
                        }
                    })
                }
            }
        }
    }
}