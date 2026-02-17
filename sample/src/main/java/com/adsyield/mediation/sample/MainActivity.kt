package com.adsyield.mediation.sample

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {
    private var nativeAd: NativeAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedIntAd: RewardedInterstitialAd? = null

    companion object {
        private const val LOG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(LOG_TAG, "onCreate")

        MobileAds.initialize(this) { initializationStatus ->
            for ((adapterClass, status) in initializationStatus.adapterStatusMap) {
                Log.d(
                    LOG_TAG,
                    "Adapter: $adapterClass, Status: ${status.description}, Latency: ${status.latency}ms",
                )
            }
            Log.d(LOG_TAG, "onInitializationComplete: $initializationStatus")
        }

        setupAdInspector()

        setupBannerAd()

        setupInterstitialAd()

        setupRewardedAd()

        setupRewardedInterstitialAd()

        setupNativeAd()
    }

    private fun setupAdInspector() {
        val adInspectorBtn = findViewById<Button>(R.id.ad_inspector_btn)
        adInspectorBtn.setOnClickListener {
            MobileAds.openAdInspector(this) { error ->
                if (error != null) {
                    Log.e(LOG_TAG, "ad inspector error: $error")
                }
            }
        }
    }

    private fun setupNativeAd() {
        val tvNativeInfo = findViewById<TextView>(R.id.tvNative)
        val templateView = findViewById<TemplateView>(R.id.templateView)
        val btnNativeLoadAd = findViewById<Button>(R.id.btnNativeLoadAd)
        val btnNativeClear = findViewById<Button>(R.id.btnNativeClear)
        enableForLoad(btnNativeLoadAd, btnNativeClear, true, templateView)
        tvNativeInfo.visibility = View.GONE
        btnNativeLoadAd.setOnClickListener {
            AdLoader.Builder(this, getString(R.string.native_ad_unit_id))
                .forNativeAd {
                    nativeAd = it
                    Log.d(LOG_TAG, "Native Ad loaded. ${it.responseInfo}")

                    val styles = NativeTemplateStyle.Builder()
                        .build()

                    templateView.setStyles(styles)
                    templateView.setNativeAd(it)

                    enableForLoad(btnNativeLoadAd, btnNativeClear, false, templateView)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        enableForLoad(btnNativeLoadAd, btnNativeClear, true, templateView)
                        Log.e(LOG_TAG, p0.message)
                        super.onAdFailedToLoad(p0)
                    }
                })
                .build()
                .loadAd(AdRequest.Builder().build())
        }

        btnNativeClear.setOnClickListener {
            templateView.destroyNativeAd()
            tvNativeInfo.text = ""
            enableForLoad(btnNativeLoadAd, btnNativeClear, true, templateView)
        }
    }

    private fun setupRewardedInterstitialAd() {
        val btnRewardedIntLoadAd = findViewById<Button>(R.id.btnRewardedIntLoadAd)
        val btnRewardedIntShowAd = findViewById<Button>(R.id.btnRewardedIntShowAd)
        enableForLoad(btnRewardedIntLoadAd, btnRewardedIntShowAd, true)
        btnRewardedIntLoadAd.setOnClickListener {
            RewardedInterstitialAd.load(
                this,
                getString(R.string.rewarded_interstitial_ad_unit_id),
                AdRequest.Builder().build(),
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        Log.d(LOG_TAG, "Rewarded Interstitial Ad was loaded.")
                        rewardedIntAd = ad
                        enableForLoad(btnRewardedIntLoadAd, btnRewardedIntShowAd, false)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(LOG_TAG, adError.message)
                        rewardedIntAd = null
                        enableForLoad(btnRewardedIntLoadAd, btnRewardedIntShowAd, true)
                    }
                },
            )
        }

        btnRewardedIntShowAd.setOnClickListener {
            if (rewardedIntAd != null) {
                rewardedIntAd?.show(this) { rewardItem ->
                    Log.d(
                        LOG_TAG,
                        "User earned the reward. -> ${rewardItem.amount} ${rewardItem.type}"
                    )
                }
                enableForLoad(btnRewardedIntLoadAd, btnRewardedIntShowAd, true)
            } else {
                Log.d(LOG_TAG, "The rewarded interstitial ad wasn't ready yet.")
            }
        }
    }

    private fun setupRewardedAd() {
        val btnRewardedLoadAd = findViewById<Button>(R.id.btnRewardedLoadAd)
        val btnRewardedShowAd = findViewById<Button>(R.id.btnRewardedShowAd)
        enableForLoad(btnRewardedLoadAd, btnRewardedShowAd, true)
        btnRewardedLoadAd.setOnClickListener {

            RewardedAd.load(
                this,
                getString(R.string.rewarded_ad_unit_id),
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(LOG_TAG, "Rewarded Ad was loaded.")
                        rewardedAd = ad
                        enableForLoad(btnRewardedLoadAd, btnRewardedShowAd, false)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(LOG_TAG, adError.message)
                        rewardedAd = null
                        enableForLoad(btnRewardedLoadAd, btnRewardedShowAd, true)
                    }
                },
            )
        }

        btnRewardedShowAd.setOnClickListener {
            if (rewardedAd != null) {
                rewardedAd?.show(this) { rewardItem ->
                    Log.d(
                        LOG_TAG,
                        "User earned the reward. -> ${rewardItem.amount} ${rewardItem.type}"
                    )
                }
                enableForLoad(btnRewardedLoadAd, btnRewardedShowAd, true)
            } else {
                Log.d(LOG_TAG, "The rewarded ad wasn't ready yet.")
            }
        }
    }

    private fun setupInterstitialAd() {
        val btnInterstitialLoad = findViewById<Button>(R.id.btnInterstitialLoadAd)
        val btnInterstitialShow = findViewById<Button>(R.id.btnInterstitialShowAd)
        enableForLoad(btnInterstitialLoad, btnInterstitialShow, true)
        btnInterstitialLoad.setOnClickListener {
            InterstitialAd.load(
                this,
                getString(R.string.interstitial_ad_unit_id),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(LOG_TAG, "Ad was loaded.")
                        interstitialAd = ad
                        enableForLoad(btnInterstitialLoad, btnInterstitialShow, false)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(LOG_TAG, adError.message)
                        interstitialAd = null
                        enableForLoad(btnInterstitialLoad, btnInterstitialShow, true)
                    }
                },
            )
        }

        btnInterstitialShow.setOnClickListener {
            if (interstitialAd != null) {
                interstitialAd?.show(this)
                enableForLoad(btnInterstitialLoad, btnInterstitialShow, true)
            } else {
                Log.d(LOG_TAG, "The interstitial ad wasn't ready yet.")
            }
        }
    }

    private fun setupBannerAd() {
        findViewById<Button>(R.id.banner_load_ad).setOnClickListener {
            val adView = getAdView(it.context, AdSize.BANNER)
            val adRequest = AdRequest.Builder().build()
            loadAd(adView, adRequest)
            val bannerContainer: FrameLayout = findViewById(R.id.banner_container)
            bannerContainer.removeAllViews()
            bannerContainer.addView(adView)
        }

        findViewById<Button>(R.id.medium_rect_load_ad).setOnClickListener {
            val adView = getAdView(it.context, AdSize.MEDIUM_RECTANGLE)
            val adRequest = AdRequest.Builder().build()
            loadAd(adView, adRequest)
            val bannerContainer: FrameLayout = findViewById(R.id.medium_rect_container)
            bannerContainer.removeAllViews()
            bannerContainer.addView(adView)
        }

        findViewById<Button>(R.id.lbanner_load_ad).visibility = View.GONE
        findViewById<Button>(R.id.lbanner_load_ad).setOnClickListener {
            val adView = getAdView(it.context, AdSize.LARGE_BANNER)
            val adRequest = AdRequest.Builder().build()
            loadAd(adView, adRequest)
            val bannerContainer: FrameLayout = findViewById(R.id.lbanner_container)
            bannerContainer.removeAllViews()
            bannerContainer.addView(adView)
        }
    }

    private fun loadAd(adView: AdView, adRequest: AdRequest) {
        adView.loadAd(adRequest)
    }

    fun getAdView(context: Context, adSize: AdSize): AdView {
        val adView = AdView(context)
        adView.apply {
            setAdSize(adSize)
            adView.adUnitId = getString(R.string.banner_ad_unit_id)
            adView.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(LOG_TAG, "Banner Ad loaded: " + adView.responseInfo)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d(LOG_TAG, "Failed to load banner ad: $loadAdError")
                    Toast.makeText(
                        this@MainActivity, "Failed to load banner: $loadAdError",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return adView
    }

    fun enableForLoad(
        loadBtn: Button,
        showBtn: Button,
        isLoad: Boolean,
        templateView: TemplateView? = null
    ) {
        if (isLoad) {
            loadBtn.isEnabled = true
            showBtn.isEnabled = false
            templateView?.visibility = View.GONE
        } else {
            loadBtn.isEnabled = false
            showBtn.isEnabled = true
            templateView?.visibility = View.VISIBLE
        }
    }
}
