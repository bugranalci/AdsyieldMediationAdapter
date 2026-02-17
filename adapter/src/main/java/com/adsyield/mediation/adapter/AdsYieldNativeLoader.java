package com.adsyield.mediation.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdsYieldNativeLoader {

    private final MediationNativeAdConfiguration adConfiguration;
    private final MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> mediationAdLoadCallback;
    private MediationNativeAdCallback nativeAdCallback;

    public AdsYieldNativeLoader(
            @NonNull MediationNativeAdConfiguration adConfiguration,
            @NonNull MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback> callback) {
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

        Log.d(AdsYieldAdapter.TAG, "Loading native ad with ad unit ID: " + adUnitId);

        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(nativeAd -> {
                    AdsYieldNativeAdMapper mapper = new AdsYieldNativeAdMapper(nativeAd);
                    nativeAdCallback = mediationAdLoadCallback.onSuccess(mapper);
                    Log.d(AdsYieldAdapter.TAG, "Native ad loaded successfully.");
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(AdsYieldAdapter.TAG, "Native ad failed to load: " + loadAdError.getMessage());
                        mediationAdLoadCallback.onFailure(loadAdError);
                    }

                    @Override
                    public void onAdOpened() {
                        if (nativeAdCallback != null) {
                            nativeAdCallback.onAdOpened();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        if (nativeAdCallback != null) {
                            nativeAdCallback.reportAdClicked();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        if (nativeAdCallback != null) {
                            nativeAdCallback.reportAdImpression();
                        }
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Maps a Google NativeAd to a UnifiedNativeAdMapper for mediation.
     */
    static class AdsYieldNativeAdMapper extends UnifiedNativeAdMapper {

        private final NativeAd nativeAd;

        public AdsYieldNativeAdMapper(@NonNull NativeAd nativeAd) {
            this.nativeAd = nativeAd;

            setHeadline(nativeAd.getHeadline());
            setBody(nativeAd.getBody());
            setCallToAction(nativeAd.getCallToAction());
            setStarRating(nativeAd.getStarRating());
            setStore(nativeAd.getStore());
            setPrice(nativeAd.getPrice());
            setAdvertiser(nativeAd.getAdvertiser());
            setOverrideImpressionRecording(true);
            setOverrideClickHandling(true);

            // Icon
            if (nativeAd.getIcon() != null) {
                setIcon(new AdsYieldNativeMappedImage(nativeAd.getIcon()));
            }

            // Images
            List<NativeAd.Image> images = nativeAd.getImages();
            if (images != null && !images.isEmpty()) {
                List<NativeAd.Image> mappedImages = new ArrayList<>();
                for (NativeAd.Image image : images) {
                    mappedImages.add(new AdsYieldNativeMappedImage(image));
                }
                setImages(mappedImages);
            }

            // MediaContent
            if (nativeAd.getMediaContent() != null) {
                setMediaContentAspectRatio(nativeAd.getMediaContent().getAspectRatio());
                setHasVideoContent(nativeAd.getMediaContent().hasVideoContent());
            }
        }

        @Override
        public void trackViews(@NonNull View containerView,
                               @NonNull Map<String, View> clickableAssetViews,
                               @NonNull Map<String, View> nonClickableAssetViews) {
            super.trackViews(containerView, clickableAssetViews, nonClickableAssetViews);
        }

        @Override
        public void untrackView(@NonNull View view) {
            super.untrackView(view);
        }
    }

    /**
     * Wraps a NativeAd.Image for use in the mapper.
     */
    static class AdsYieldNativeMappedImage extends NativeAd.Image {

        private final Drawable drawable;
        private final Uri uri;
        private final double scale;

        public AdsYieldNativeMappedImage(@NonNull NativeAd.Image image) {
            this.drawable = image.getDrawable();
            this.uri = image.getUri();
            this.scale = image.getScale();
        }

        @Nullable
        @Override
        public Drawable getDrawable() {
            return drawable;
        }

        @Nullable
        @Override
        public Uri getUri() {
            return uri;
        }

        @Override
        public double getScale() {
            return scale;
        }
    }
}
