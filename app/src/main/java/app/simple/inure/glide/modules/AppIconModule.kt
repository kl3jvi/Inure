package app.simple.inure.glide.modules

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import app.simple.inure.R
import app.simple.inure.glide.icon.AppIcon
import app.simple.inure.glide.icon.AppIconLoader
import app.simple.inure.glide.transformation.BlurShadow
import app.simple.inure.glide.transformation.Padding
import app.simple.inure.preferences.AppearancePreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class AppIconModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    @SuppressLint("CheckResult")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultTransitionOptions(Bitmap::class.java, BitmapTransitionOptions.withCrossFade())

        val requestOptions = RequestOptions()

        requestOptions.format(DecodeFormat.PREFER_ARGB_8888)
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        requestOptions.fallback(R.drawable.ic_app_icon)
        requestOptions.error(R.drawable.ic_app_icon)

        if (AppearancePreferences.isIconShadowsOn()) {
            requestOptions.transform(
                Padding(
                    BlurShadow.RENDERSCRIPT_DEFAULT_SHADOW_SIZE.toInt()), BlurShadow(context)
                        .setElevation(25F)
                        .setBlurRadius(BlurShadow.RENDERSCRIPT_DEFAULT_SHADOW_SIZE))
        } else {
            requestOptions.transform(
                Padding(
                    BlurShadow.RENDERSCRIPT_DEFAULT_SHADOW_SIZE.toInt()))
        }

        builder.setDefaultRequestOptions(requestOptions)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(AppIcon::class.java, Bitmap::class.java, AppIconLoader.Factory())
        //registry.append(AppGraphicsModel::class.java, InputStream::class.java, AppGraphicsLoader.Factory())
    }
}