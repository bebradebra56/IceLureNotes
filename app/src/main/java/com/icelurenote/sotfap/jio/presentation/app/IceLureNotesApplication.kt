package com.icelurenote.sotfap.jio.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.icelurenote.sotfap.jio.presentation.di.iceLureNotesModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface IceLureNotesAppsFlyerState {
    data object IceLureNotesDefault : IceLureNotesAppsFlyerState
    data class IceLureNotesSuccess(val iceLureNotesData: MutableMap<String, Any>?) :
        IceLureNotesAppsFlyerState

    data object IceLureNotesError : IceLureNotesAppsFlyerState
}

interface IceLureNotesAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ICE_LURE_NOTES_LIN)
    fun iceLureNotesGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ICE_LURE_NOTES_APP_DEV = "5vHyNHFpNbSkXpibd6WbeF"
private const val ICE_LURE_NOTES_LIN = "com.icelurenote.sotfap"

class IceLureNotesApplication : Application() {

    private var iceLureNotesIsResumed = false
//    private var iceLureNotesConversionTimeoutJob: Job? = null
    private var iceLureNotesDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        iceLureNotesSetDebufLogger(appsflyer)
        iceLureNotesMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        iceLureNotesExtractDeepMap(p0.deepLink)
                        Log.d(ICE_LURE_NOTES_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ICE_LURE_NOTES_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ICE_LURE_NOTES_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ICE_LURE_NOTES_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                    iceLureNotesConversionTimeoutJob?.cancel()
                    Log.d(ICE_LURE_NOTES_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = iceLureNotesGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.iceLureNotesGetClient(
                                    devkey = ICE_LURE_NOTES_APP_DEV,
                                    deviceId = iceLureNotesGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ICE_LURE_NOTES_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    iceLureNotesResume(
                                        IceLureNotesAppsFlyerState.IceLureNotesSuccess(
                                            p0
                                        )
                                    )
                                } else {
                                    iceLureNotesResume(
                                        IceLureNotesAppsFlyerState.IceLureNotesSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(ICE_LURE_NOTES_MAIN_TAG, "Error: ${d.message}")
                                iceLureNotesResume(IceLureNotesAppsFlyerState.IceLureNotesError)
                            }
                        }
                    } else {
                        iceLureNotesResume(IceLureNotesAppsFlyerState.IceLureNotesSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
//                    iceLureNotesConversionTimeoutJob?.cancel()
                    Log.d(ICE_LURE_NOTES_MAIN_TAG, "onConversionDataFail: $p0")
                    iceLureNotesResume(IceLureNotesAppsFlyerState.IceLureNotesError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ICE_LURE_NOTES_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ICE_LURE_NOTES_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ICE_LURE_NOTES_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ICE_LURE_NOTES_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ICE_LURE_NOTES_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
//        iceLureNotesStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@IceLureNotesApplication)
            modules(
                listOf(
                    iceLureNotesModule
                )
            )
        }
    }

    private fun iceLureNotesExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ICE_LURE_NOTES_MAIN_TAG, "Extracted DeepLink data: $map")
        iceLureNotesDeepLinkData = map
    }

//    private fun iceLureNotesStartConversionTimeout() {
//        iceLureNotesConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            if (!iceLureNotesIsResumed) {
//                Log.d(PLINK_ZEN_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
//                iceLureNotesResume(PlinkZenAppsFlyerState.PlinkZenError)
//            }
//        }
//    }

    private fun iceLureNotesResume(state: IceLureNotesAppsFlyerState) {
//        iceLureNotesConversionTimeoutJob?.cancel()
        if (state is IceLureNotesAppsFlyerState.IceLureNotesSuccess) {
            val convData = state.iceLureNotesData ?: mutableMapOf()
            val deepData = iceLureNotesDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!iceLureNotesIsResumed) {
                iceLureNotesIsResumed = true
                iceLureNotesConversionFlow.value =
                    IceLureNotesAppsFlyerState.IceLureNotesSuccess(merged)
            }
        } else {
            if (!iceLureNotesIsResumed) {
                iceLureNotesIsResumed = true
                iceLureNotesConversionFlow.value = state
            }
        }
    }

    private fun iceLureNotesGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ICE_LURE_NOTES_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun iceLureNotesSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun iceLureNotesMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun iceLureNotesGetApi(url: String, client: OkHttpClient?): IceLureNotesAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var iceLureNotesInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val iceLureNotesConversionFlow: MutableStateFlow<IceLureNotesAppsFlyerState> = MutableStateFlow(
            IceLureNotesAppsFlyerState.IceLureNotesDefault
        )
        var ICE_LURE_NOTES_FB_LI: String? = null
        const val ICE_LURE_NOTES_MAIN_TAG = "IceLureNotesMainTag"
    }
}