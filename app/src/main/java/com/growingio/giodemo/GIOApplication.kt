package com.growingio.giodemo

import android.app.Application
import android.content.Intent
import android.util.Log
import com.growingio.android.sdk.collection.Configuration
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.deeplink.DeeplinkCallback
import com.growingio.android.sdk.gtouch.GrowingTouch
import com.growingio.android.sdk.gtouch.config.GTouchConfig
import com.growingio.android.sdk.gtouch.listener.EventPopupListener

/**
 * classDesc: Application , 初始化 GrowingIO SDK
 */

const val TAG: String = "GIOApplication"

class GIOApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val context = this

        GrowingIO.startWithConfiguration(
            this, Configuration()
                .trackAllFragments()
                .setTestMode(BuildConfig.DEBUG)
                .setDebugMode(BuildConfig.DEBUG)
                .setChannel(BuildConfig.CHANNEL)
//              ------Demo 环境， 请勿修改------
                .setDataHost("https://demo1.growingio.com")
                .setReportHost("https://demo1gta.growingio.com")
                .setTrackerHost("https://apifwd.growingio.com")
                .setGtaHost("https://demo1gta.growingio.com")
                .setWsHost("wss://demo1gta.growingio.com")
//              ------Demo 环境， 请勿修改------
//              - 广告监测短链： https://gio.ren/PQ2r6aaidoMo ，自定义参数：{"param":"GIO 马克杯","jumpTo":"productDetail"}
                .setDeeplinkCallback(object : DeeplinkCallback {
                    override fun onReceive(
                        params: MutableMap<String, String>,
                        errorCode: Int,
                        appAwakePassedTime: Long
                    ) {
                        // sdk2.8.4+，在 DeepLink callback 中新增回调参数 appAwakePassedTime ：
                        //单位毫秒，App 唤醒到收到 GIO callback 的时间，用以判断网络状态不好的情况，应用已经打开很久，才收到回调，开发人员决定是否收到参数后仍然跳转自定义的指定页面。
                        //当返回值为 0 的时候，为 DeepLink 方式打开。
                        // 接口说明文档地址： https://docs.growingio.com/docs/sdk-integration/android-sdk/android-sdk#deep-link-hui-tiao-can-shu-huo-qu
                        if (errorCode == DeeplinkCallback.SUCCESS && appAwakePassedTime < 3000 && params["jumpTo"] != null && params["param"] != null) {
                            if ("productDetail" == params["jumpTo"])
                                startActivity(
                                    Intent(
                                        context,
                                        ProductDetailActivity::class.java
                                    ).putExtra(
                                        "product",
                                        params["param"]
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                        }
                    }
                })


        )
        GrowingIO.getInstance().userId = "GIOXiaoYing"

        GrowingTouch.startWithConfig(
            this, GTouchConfig()
                .setDebugEnable(true)
                .setEventPopupEnable(true)
                .setUploadExceptionEnable(true)
                .setEventPopupListener(object : EventPopupListener {
                    override fun onLoadFailed(
                        eventId: String?,
                        eventType: String?,
                        errorCode: Int,
                        description: String?
                    ) {
                        Log.e(
                            TAG,
                            "onLoadFailed: eventId = $eventId, eventType = $eventType， description = $description"
                        )
                    }

                    override fun onCancel(eventId: String?, eventType: String?) {
                        Log.e(TAG, "onCancel: eventId = $eventId, eventType = $eventType")
                    }

                    override fun onLoadSuccess(eventId: String?, eventType: String?) {
                        Log.e(TAG, "onLoadSuccess: eventId = $eventId, eventType = $eventType")
                    }

                    override fun onClicked(
                        eventId: String?,
                        eventType: String?,
                        openUrl: String?
                    ): Boolean {
                        Log.e(
                            TAG,
                            "onClicked: eventId = $eventId, eventType = $eventType openUrl = $openUrl"
                        )
                        return false
                    }

                    override fun onTimeout(eventId: String?, eventType: String?) {
                        Log.e(TAG, "onTimeout: eventId = $eventId, eventType = $eventType")
                    }

                })
        )

        //会员等级
        GrowingIO.getInstance().setPeopleVariable("vipLevel", 1)

    }
}
