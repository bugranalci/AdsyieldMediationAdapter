package com.adsyield.mediation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;

public class AdsYieldBannerLoader implements MediationBannerAd {

    private final MediationBannerAdConfiguration adConfiguration;
    private final MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mediationAdLoadCallback;
    private MediationBannerAdCallback bannerAdCallback;
    private AdView adView;

    public AdsYieldBannerLoader(
            @NonNull MediationBannerAdConfiguration adConfiguration,
            @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        this.adConfiguration = adConfiguration;
        this.mediationAdLoadCallback = callback;
    }

    public void loadAd() {
        String adUnitId = adConfiguration.getServerParameters()
                .getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);

        if (TextUtils.isEmpty(adUnitId)) {
            AdError error = new AdError(101, "Missing ad unit ID", "com.adsyield.mediation.adapter");
            Log.e(AdsYieldAdapter.TAG, error.getMessage());
            mediationAdLoadCallback.onFailure(error);
            return;
        }

        Context context = adConfiguration.getContext();
        AdSize adSize = adConfiguration.getAdSize();

        Log.d(AdsYieldAdapter.TAG, "Loading banner ad with ad unit ID: " + adUnitId);

        adView = new AdView(context);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(adSize);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(AdsYieldAdapter.TAG, "Banner ad loaded successfully.");
                bannerAdCallback = mediationAdLoadCallback.onSuccess(AdsYieldBannerLoader.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(AdsYieldAdapter.TAG, "Banner ad failed to load: " + loadAdError.getMessage());
                mediationAdLoadCallback.onFailure(loadAdError);
            }

            @Override
            public void onAdOpened() {
                if (bannerAdCallback != null) {
                    bannerAdCallback.onAdOpened();
                }
            }

            @Override
            public void onAdClosed() {
                if (bannerAdCallback != null) {
                    bannerAdCallback.onAdClosed();
                }
            }

            @Override
            public void onAdClicked() {
                if (bannerAdCallback != null) {
                    bannerAdCallback.reportAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
                if (bannerAdCallback != null) {
                    bannerAdCallback.reportAdImpression();
                }
            }
        });

        adView.loadAd(new AdRequest.Builder().build());
    }

    @NonNull
    @Override
    public View getView() {
        return adView;
    }
}
