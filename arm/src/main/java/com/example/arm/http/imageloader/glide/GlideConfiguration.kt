package com.example.arm.http.imageloader.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.arm.base.BaseApplication
import com.example.arm.http.OkHttpUrlLoader
import com.example.arm.http.imageloader.BaseImageLoaderStrategy
import okhttp3.OkHttpClient
import org.kodein.di.direct
import org.kodein.di.instance
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.jvm.Throws

@GlideModule(glideName = "ArmsForkGlide")
class GlideConfiguration : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        //Glide默认设置MemoryCacheSize为2倍CacheScreens,BitmapPool为4或1倍CacheScreens(Android O的硬件加速使得BitmapPool没有那么重要),更贴合实际
        /*MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).setMemoryCacheScreens().build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));*/
        val di = BaseApplication.INSTANCE.di
        val loadImgStrategy: BaseImageLoaderStrategy<*> = di.direct.instance()
        if (loadImgStrategy is GlideAppliesOption) {
            (loadImgStrategy as GlideAppliesOption).applyOptions(context, builder)
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
        val di = BaseApplication.INSTANCE.di
        try {
            registry.replace(
                GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(
                    sSLOkHttpClient
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val loadImgStrategy: BaseImageLoaderStrategy<*> = di.direct.instance()
        if (loadImgStrategy is GlideAppliesOption) {
            (loadImgStrategy as GlideAppliesOption).registerComponents(context, glide, registry)
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    @get:Throws(Exception::class)
    private val sSLOkHttpClient: OkHttpClient
        private get() {
            val trustManager: X509TrustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(size = 0)
                }
            }
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier { hostname, session -> true }
                .build()
        }
}