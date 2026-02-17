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
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class AdsYieldRewardedInterstitialLoader implements MediationRewardedAd {

    private final MediationRewardedAdConfiguration adConfiguration;
    private final MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback;
    private MediationRewardedAdCallback rewardedAdCallback;
    private RewardedInterstitialAd rewardedInterstitialAd;

    public AdsYieldRewardedInterstitialLoader(
            @NonNull MediationRewardedAdConfiguration adConfiguration,
            @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback) {
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

        Log.d(AdsYieldAdapter.TAG, "Loading rewarded interstitial ad with ad unit ID: " + adUnitId);

        RewardedInterstitialAd.load(context, adUnitId, new AdRequest.Builder().build(),
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        Log.d(AdsYieldAdapter.TAG, "Rewarded interstitial ad loaded successfully.");
                        rewardedInterstitialAd = ad;
                        rewardedAdCallback = mediationAdLoadCallback.onSuccess(AdsYieldRewardedInterstitialLoader.this);

                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                if (rewardedAdCallback != null) {
                                    rewardedAdCallback.reportAdImpression();
                                    rewardedAdCallback.onAdOpened();
                                    rewardedAdCallback.onVideoStart();
                                }
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (rewardedAdCallback != null) {
                                    rewardedAdCallback.onVideoComplete();
                                    rewardedAdCallback.onAdClosed();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.e(AdsYieldAdapter.TAG, "Rewarded interstitial ad failed to show: " + adError.getMessage());
                            }

                            @Override
                            public void onAdClicked() {
                                if (rewardedAdCallback != null) {
                                    rewardedAdCallback.reportAdClicked();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(AdsYieldAdapter.TAG, "Rewarded interstitial ad failed to load: " + loadAdError.getMessage());
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
            if (rewardedAdCallback != null) {
                rewardedAdCallback.onAdFailedToShow(error);
            }
            return;
        }

        if (rewardedInterstitialAd == null) {
            AdError error = new AdError(103, "Ad not loaded", "com.adsyield.mediation.adapter");
            Log.e(AdsYieldAdapter.TAG, error.getMessage());
            if (rewardedAdCallback != null) {
                rewardedAdCallback.onAdFailedToShow(error);
            }
            return;
        }

        rewardedInterstitialAd.show((Activity) context, rewardItem -> {
            if (rewardedAdCallback != null) {
                rewardedAdCallback.onUserEarnedReward(rewardItem);
            }
        });
    }
}
