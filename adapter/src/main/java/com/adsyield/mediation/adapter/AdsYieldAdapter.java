package com.adsyield.mediation.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;

import java.util.List;

@Keep
public class AdsYieldAdapter extends Adapter {

    public static final String TAG = "AdsYieldAdapter";

    public static final int ADAPTER_VERSION_MAJOR = 1;
    public static final int ADAPTER_VERSION_MINOR = 0;
    public static final int ADAPTER_VERSION_PATCH = 0;

    private AdsYieldBannerLoader bannerLoader;
    private AdsYieldInterstitialLoader interstitialLoader;
    private AdsYieldRewardedLoader rewardedLoader;
    private AdsYieldRewardedInterstitialLoader rewardedInterstitialLoader;
    private AdsYieldNativeLoader nativeLoader;

    @Override
    public void initialize(@NonNull Context context,
                           @NonNull InitializationCompleteCallback callback,
                           @NonNull List<MediationConfiguration> configurations) {
        Log.d(TAG, "AdsYield Adapter initialized successfully.");
        callback.onInitializationSucceeded();
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        return new VersionInfo(ADAPTER_VERSION_MAJOR, ADAPTER_VERSION_MINOR, ADAPTER_VERSION_PATCH);
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        try {
            String versionString = MobileAds.getVersion().toString();
            String[] parts = versionString.split("\\.");
            if (parts.length >= 3) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                int patch = Integer.parseInt(parts[2]);
                return new VersionInfo(major, minor, patch);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse Google Ads SDK version.", e);
        }
        return new VersionInfo(0, 0, 0);
    }

    @Override
    public void loadBannerAd(@NonNull MediationBannerAdConfiguration adConfiguration,
                             @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        bannerLoader = new AdsYieldBannerLoader(adConfiguration, callback);
        bannerLoader.loadAd();
    }

    @Override
    public void loadInterstitialAd(@NonNull MediationInterstitialAdConfiguration adConfiguration,
                                   @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
        interstitialLoader = new AdsYieldInterstitialLoader(adConfiguration, callback);
        interstitialLoader.loadAd();
    }

    @Override
    public void loadRewardedAd(@NonNull MediationRewardedAdConfiguration adConfiguration,
                               @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        rewardedLoader = new AdsYieldRewardedLoader(adConfiguration, callback);
        rewardedLoader.loadAd();
    }

    @Override
    public void loadRewardedInterstitialAd(@NonNull MediationRewardedAdConfiguration adConfiguration,
                                           @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
        rewardedInterstitialLoader = new AdsYieldRewardedInterstitialLoader(adConfiguration, callback);
        rewardedInterstitialLoader.loadAd();
    }

    @Override
    public void loadNativeAd(@NonNull MediationNativeAdConfiguration adConfiguration,
                             @NonNull MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> callback) {
        nativeLoader = new AdsYieldNativeLoader(adConfiguration, callback);
        nativeLoader.loadAd();
    }
}
