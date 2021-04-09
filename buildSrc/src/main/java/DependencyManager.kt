/**
 * <pre>
 *     author: dhl
 *     date  : 2020/7/3
 *     desc  : 如果数量少的话，放在一个类里面就可以，如果数量多的话，可以拆分为多个类
 * </pre>
 */

object Versions {
    const val androidSupportSdkVersion = "28.0.0"
    const val androidXSdkVersion = "1.1.0"
    const val retrofitSdkVersion = "2.9.0"
    const val dagger2SdkVersion = "2.26"
    const val glideSdkVersion = "4.12.0"
    const val butterknifeSdkVersion = "10.2.3"
    const val rxlifecycleSdkVersion = "1.0"
    const val rxlifecycle2SdkVersion = "2.2.2"
    const val espressoSdkVersion = "3.3.0"
    const val canarySdkVersion = "2.2"
    const val kotlin_version = "1.4.32"
    const val coroutines_version = "1.3.1"
    const val kodein_version = "7.0.0"
}

object Depend {
    // kotlin
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_version}"
    const val kotlinAndroid = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin_version}"

    // 后续不需要添加该库了,gradle自动添加
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:1.4.31"
    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.2"
    const val activityKtx = "androidx.activity:activity-ktx:1.2.2"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"

    //协程
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines_version}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"

    //kodein
    const val kodeinCore = "org.kodein.di:kodein-di-framework-android-core:${Versions.kodein_version}"
    const val kodeinSupport = "org.kodein.di:kodein-di-framework-android-support:${Versions.kodein_version}"
    const val kodeinX = "org.kodein.di:kodein-di-framework-android-x:${Versions.kodein_version}"

    //support
    const val appcompatV7 = "com.android.support:appcompat-v7:${Versions.androidSupportSdkVersion}"
    const val supportDesign = "com.android.support:design:${Versions.androidSupportSdkVersion}"
    const val supportV4 = "com.android.support:support-v4:${Versions.androidSupportSdkVersion}"
    const val cardviewV7 = "com.android.support:cardview-v7:${Versions.androidSupportSdkVersion}"
    const val supportAnnotations = "com.android.support:support-annotations:${Versions.androidSupportSdkVersion}"
    const val recyclerviewV7 = "com.android.support:recyclerview-v7:${Versions.androidSupportSdkVersion}"

    //androidx
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val design = "com.google.android.material:material:1.3.0"
    const val legacySupportV4 = "androidx.legacy:legacy-support-v4:1.0.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val annotations = "androidx.annotation:annotation:1.2.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val multidex = "androidx.multidex:multidex:2.0.1"
    const val viewpager = "androidx.viewpager:viewpager:1.0.0"
    const val core = "androidx.core:core:${Versions.androidXSdkVersion}"

    //network
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitSdkVersion}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofitSdkVersion}"
    const val retrofitConverterScalar = "com.squareup.retrofit2:converter-scalars:${Versions.retrofitSdkVersion}"
    const val retrofitAdapterRxjava = "com.squareup.retrofit2:adapter-rxjava:${Versions.retrofitSdkVersion}"
    const val retrofitAdapterRxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofitSdkVersion}"
    const val okhttp3 = "com.squareup.okhttp3:okhttp:3.12.9"
    const val okhttp4 = "com.squareup.okhttp3:okhttp:4.0.0"
    const val okhttpUrlconnection = "com.squareup.okhttp:okhttp-urlconnection:2.0.0"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:4.9.0"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glideSdkVersion}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideSdkVersion}"
    const val glideLoaderOkhttp3 = "com.github.bumptech.glide:okhttp3-integration:${Versions.glideSdkVersion}"
    const val picasso = "com.squareup.picasso:picasso:2.5.2"

    //view
    const val autolayout = "com.zhy:autolayout:1.4.5"
    const val butterknife = "com.jakewharton:butterknife:${Versions.butterknifeSdkVersion}"
    const val butterknifeCompiler = "com.jakewharton:butterknife-compiler:${Versions.butterknifeSdkVersion}"
    const val pickerview = "com.contrarywind:Android-PickerView:4.1.9"
    const val photoview = "com.github.chrisbanes.photoview:library:1.2.3"
    const val numberprogressbar = "com.daimajia.numberprogressbar:library:1.2@aar"
    const val nineoldandroids = "com.nineoldandroids:library:2.4.0"
    const val paginate = "com.github.markomilos:paginate:0.5.1"
    const val vlayout = "com.alibaba.android:vlayout:1.1.0@aar"
    const val autosize = "me.jessyan:autosize:1.2.0"
    const val refreshKernel = "com.scwang.smart:refresh-layout-kernel:2.0.3"
    const val refreshClassics = "com.scwang.smart:refresh-header-classics:2.0.3"

    //rx1
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.0.2"
    const val rxjava = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val rxlifecycle = "com.trello:rxlifecycle:${Versions.rxlifecycleSdkVersion}"
    const val rxlifecycleComponents = "com.trello:rxlifecycle-components:${Versions.rxlifecycleSdkVersion}"
    const val rxcache = "com.github.VictorAlbertos.RxCache:runtime:1.7.0-1.x"
    const val rxcacheJolyglotGson = "com.github.VictorAlbertos.Jolyglot:gson:0.0.6"
    const val rxbindingRecyclerviewV7 = "com.jakewharton.rxbinding:rxbinding-recyclerview-v7:1.0.1"
    const val rxpermissions = "com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar"
    const val rxerrorhandler = "me.jessyan:rxerrorhandler:1.0.1"

    //rx2
    const val rxandroid2 = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val rxlifecycle2 = "com.trello.rxlifecycle2:rxlifecycle:${Versions.rxlifecycle2SdkVersion}"
    const val rxlife2Android = "com.trello.rxlifecycle2:rxlifecycle-android:${Versions.rxlifecycle2SdkVersion}"
    const val rxlife2Components = "com.trello.rxlifecycle2:rxlifecycle-components:${Versions.rxlifecycle2SdkVersion}"
    const val rxcache2 = "com.github.VictorAlbertos.RxCache:runtime:1.8.3-2.x"
    const val rxpermissions2 = "com.github.tbruyelle:rxpermissions:0.12"
    const val rxerrorhandler2 = "me.jessyan:rxerrorhandler:2.1.1"

    //tools
    const val dagger2 = "com.google.dagger:dagger:2.34"
    const val dagger2Android = "com.google.dagger:dagger-android:${Versions.dagger2SdkVersion}"
    const val dagger2AndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger2SdkVersion}"
    const val dagger2Compiler = "com.google.dagger:dagger-compiler:2.34"
    const val dagger2AndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger2SdkVersion}"
    const val androideventbus = "org.simple:androideventbus:1.0.5.1"
    const val eventbus = "org.greenrobot:eventbus:3.2.0"
    const val otto = "com.squareup:otto:1.3.8"
    const val gson = "com.google.code.gson:gson:2.8.6"
    const val javaxAnnotation = "javax.annotation:jsr250-api:1.0"
    const val arouter = "com.alibaba:arouter-api:1.5.0"
    const val arouterCompiler = "com.alibaba:arouter-compiler:1.1.4"
    const val progressmanager = "me.jessyan:progressmanager:1.5.0"
    const val retrofitUrlManager = "me.jessyan:retrofit-url-manager:1.4.0"
    const val lifecyclemodel = "me.jessyan:lifecyclemodel:1.0.1"
    const val cymchadRecyclerAdapter = "com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.47"
    const val flowlayout = "com.hyman:flowlayout-lib:1.1.2"
    const val galleryfinal = "cn.finalteam:galleryfinal:1.4.8.7"
    const val statusbarutil = "com.jaeger.statusbarutil:library:1.5.1"
    const val permission = "com.yanzhenjie:permission:2.0.0-rc12"
    const val Luban = "top.zibin:Luban:1.1.8"
    const val googleGeocoder = "com.googlecode.libphonenumber:geocoder:2.140"
    const val googleLibphonenumber = "com.googlecode.libphonenumber:libphonenumber:8.12.4"
    const val zxingscanner = "com.mylhyl:zxingscanner:2.1.6"
    const val gaodeMap = "com.amap.api:map2d:latest.integration"
    const val gaodeLocation = "com.amap.api:location:latest.integration"
    const val gaodeSearch = "com.amap.api:search:latest.integration"
    const val ExternalMapUtils = "com.github.MZCretin:ExternalMapUtils:v1.1.0"

    //test
    const val junit = "junit:junit:4.13.2"
    const val androidxUnit = "androidx.test.ext:junit:1.1.2"
    const val androidJUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val runner = "com.android.support.test:runner:1.0.2"
    const val espressoCore = "com.android.support.test.espresso:espresso-core:3.0.2"
    const val espressoContrib = "com.android.support.test.espresso:espresso-contrib:${Versions.espressoSdkVersion}"
    const val espressoIntents = "com.android.support.test.espresso:espresso-intents:${Versions.espressoSdkVersion}"
    const val mockitoCore = "org.mockito:mockito-core:1.0"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val logger = "com.orhanobut:logger:2.2.0"
    const val canaryDebug = "com.squareup.leakcanary:leakcanary-android:${Versions.canarySdkVersion}"
    const val canaryRelease = "com.squareup.leakcanary:leakcanary-android-no-op:1.6.3"
    const val umengAnalytics = "com.umeng.analytics:analytics:6.0.1"

}