package app.simple.inure.viewmodels.viewers

import android.app.Application
import android.content.pm.PackageInfo
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.exceptions.StringTooLargeException
import app.simple.inure.preferences.ConfigurationPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class JSONViewerViewModel(application: Application, private val accentColor: Int, private val packageInfo: PackageInfo, private val path: String)
    : AndroidViewModel(application) {

    private val quotations: Pattern = Pattern.compile(":\\s\"[\\S\\w^]*\"",
                                                      Pattern.MULTILINE or Pattern.CASE_INSENSITIVE)

    private val tags = Pattern.compile("\"[a-zA-Z_0-9]+\"+:",  // "a-z0-9":
                                       Pattern.MULTILINE or Pattern.CASE_INSENSITIVE)

    private val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val spanned: MutableLiveData<Spanned> by lazy {
        MutableLiveData<Spanned>().also {
            getSpannedXml()
        }
    }

    fun getSpanned(): LiveData<Spanned> {
        return spanned
    }

    fun getError(): LiveData<String> {
        return error
    }

    private fun getSpannedXml() {
        viewModelScope.launch(Dispatchers.IO) {

            delay(500L)

            kotlin.runCatching {
                val formattedContent: SpannableString

                val code: String = getJsonFile()!!

                if (code.length >= 150000 && !ConfigurationPreferences.isLoadingLargeStrings()) {
                    throw StringTooLargeException("String size ${code.length} is too big to render without freezing the app")
                }

                formattedContent = SpannableString(code)
                val matcher: Matcher = tags.matcher(code)
                while (matcher.find()) {
                    formattedContent.setSpan(ForegroundColorSpan(Color.parseColor("#2980B9")), matcher.start(),
                                             matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                matcher.usePattern(quotations)
                while (matcher.find()) {
                    formattedContent.setSpan(ForegroundColorSpan(accentColor),
                                             matcher.start(), matcher.end(),
                                             Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                spanned.postValue(formattedContent)
            }.getOrElse {
                error.postValue(it.stackTraceToString())
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun getJsonFile(): String? {
        ZipFile(packageInfo.applicationInfo.sourceDir).use { zipFile ->
            val entries: Enumeration<out ZipEntry?> = zipFile.entries()

            while (entries.hasMoreElements()) {
                entries.nextElement()!!.let { entry ->
                    if (entry.name == path) {
                        return IOUtils.toString(
                            BufferedInputStream(zipFile.getInputStream(entry)),
                            "UTF-8")
                    }
                }
            }
        }

        throw FileNotFoundException("JSON file not found")
    }
}