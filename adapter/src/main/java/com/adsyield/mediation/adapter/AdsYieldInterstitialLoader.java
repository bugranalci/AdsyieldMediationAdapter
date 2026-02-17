package com.adsyield.mediation.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;

public class AdsYieldInterstitialLoader implements MediationInterstitialAd {

    private final MediationInterstitialAdConfiguration adConfiguration;
    private final MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> mediationAdLoadCallback;
    private MediationInterstitialAdCallback interstitialAdCallback;
    private InterstitialAd interstitialAd;

    public AdsYieldInterstitialLoader(
            @NonNull MediationInterstitialAdConfiguration adConfiguration,
            @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback) {
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

        Log.d(AdsYieldAdapter.TAG, "Loading interstitial ad with ad unit ID: " + adUnitId);

        InterstitialAd.load(context, adUnitId, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        Log.d(AdsYieldAdapter.TAG, "Interstitial ad loaded successfully.");
                        interstitialAd = ad;
                        interstitialAdCallback = mediationAdLoadCallback.onSuccess(AdsYieldInterstitialLoader.this);

                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                if (interstitialAdCallback != null) {
                                    interstitialAdCallback.reportAdImpression();
                                    interstitialAdCallback.onAdOpened();
                                }
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (interstitialAdCallback != null) {
                                    interstitialAdCallback.onAdClosed();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.e(AdsYieldAdapter.TAG, "Interstitial ad failed to show: " + adError.getMessage());
                            }

                            @Override
                            public void onAdClicked() {
                                if (interstitialAdCallback != null) {
                                    interstitialAdCallback.reportAdClicked();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(AdsYieldAdapter.TAG, "Interstitial ad failed to load: " + loadAdError.getMessage());
                        mediationAdLoadCallback.onFailure(loadAdError);
                    }
                });
    }

    // Note: This method should be called from the main (UI) thread.
    @Override
    public void showAd(@NonNull Context context) {
        if (!(context instanceof Activity)) {
            AdError error = new AdError(102, "Context is not an Activity", "com.adsyield.mediation.adapter");
            Log.e(AdsYieldAdapter.TAG, error.getMessage());
            if (interstitialAdCallback != null) {
                interstitialAdCallback.onAdFailedToShow(error);
            }
            return;
        }

        if (interstitialAd == null) {
            AdError error = new AdError(103, "Ad not loaded", "com.adsyield.mediation.adapter");
            Log.e(AdsYieldAdapter.TAG, error.getMessage());
            if (interstitialAdCallback != null) {
                interstitialAdCallback.onAdFailedToShow(error);
            }
            return;
        }

        interstitialAd.show((Activity) context);
    }
}
