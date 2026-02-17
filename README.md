# AdsYield Mediation Adapter for Android

AdsYield Mediation Adapter, Google AdMob ve Google Ad Manager mediation ile AdsYield demand'ini uygulamanıza entegre etmenizi sağlar.

## Kurulum

### JitPack ile

settings.gradle.kts dosyanıza ekleyin:

    maven { url = uri("https://jitpack.io") }

build.gradle.kts dosyanıza ekleyin:

    implementation("com.github.bugranalci:AdsyieldMediationAdapter:1.0.2")

## Desteklenen Formatlar

- Banner (320x50, 300x250, Adaptive)
- Interstitial
- Rewarded
- Rewarded Interstitial
- Native

## AdMob Custom Event Ayarları

- **Class Name:** `com.adsyield.mediation.adapter.AdsYieldAdapter`
- **Parameter:** AdsYield tarafından sağlanan Ad Unit ID

## Dokümantasyon

Detaylı entegrasyon rehberi için [docs/ENTEGRASYON_REHBERI.md](docs/ENTEGRASYON_REHBERI.md) dosyasına bakın.

## Lisans

Apache License 2.0
