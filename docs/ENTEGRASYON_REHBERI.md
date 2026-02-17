# AdsYield Mediation Adapter - Entegrasyon Rehberi

## 1. Gereksinimler

- Android API Level 23+
- Google Mobile Ads SDK 24.7.0+
- AdMob veya Google Ad Manager hesabı

## 2. Gradle Kurulumu

### Yöntem 1: JitPack ile (Önerilen)

Proje seviyesi `settings.gradle.kts` dosyasına JitPack repository'sini ekleyin:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

App seviyesi `build.gradle.kts` dosyasına dependency'yi ekleyin:

```kotlin
dependencies {
    implementation("com.github.bugranalci:AdsyieldMediationAdapter:1.0.0")
}
```

### Yöntem 2: AAR dosyası ile

```gradle
// app/libs klasörüne adsyield-mediation-adapter.aar dosyasını kopyalayın
dependencies {
    implementation(files("libs/adsyield-mediation-adapter.aar"))
    implementation("com.google.android.gms:play-services-ads:24.7.0")
}
```

**Not:** AAR yöntemiyle kurulumda Google Ads SDK'yı ayrıca eklemeniz gerekir. JitPack yöntemi bunu otomatik olarak çeker.

> **Versiyon Uyumluluk Notu:** Projenizde zaten Google Mobile Ads SDK dependency'si varsa, adapter'ın kullandığı versiyon (24.7.0) ile uyumlu olduğundan emin olun. Versiyon çakışması durumunda Gradle resolution strategy kullanabilirsiniz.

## 3. AdMob'da Custom Event Oluşturma

1. AdMob hesabınıza giriş yapın: https://admob.google.com
2. Sol menüden **Mediation > Waterfall sources** seçin
3. **"Set up ad source"** butonuna tıklayın > **"Add custom event"** seçin
4. Mapping ekleyin:
   - **Mapping Name:** AdsYield
   - **Class Name:** `com.adsyield.mediation.adapter.AdsYieldAdapter`
   - **Parameter:** AdsYield tarafından sağlanan Ad Unit ID (örn: `ca-app-pub-XXXXX/YYYYYY`)
5. Bu custom event'i mediation grubunuza ekleyin ve eCPM değerini belirleyin

## 4. GAM (Google Ad Manager) İçin

Aynı adımlar GAM'da da geçerlidir:

- **Yield Groups > Add yield partner > Custom event** yolunu izleyin
- Class name ve parameter aynıdır

## 5. Desteklenen Reklam Formatları

| Format | Açıklama | Dokümantasyon |
|--------|----------|---------------|
| Banner | 320x50, 300x250, Adaptive Banner | [Google Banner Docs](https://developers.google.com/admob/android/banner?hl=tr) |
| Interstitial | Tam ekran geçiş reklamı | [Google Interstitial Docs](https://developers.google.com/admob/android/interstitial?hl=tr) |
| Rewarded | Ödüllü video reklam | [Google Rewarded Docs](https://developers.google.com/admob/android/rewarded?hl=tr) |
| Rewarded Interstitial | Ödüllü geçiş reklamı | [Google Rewarded Interstitial Docs](https://developers.google.com/admob/android/rewarded-interstitial?hl=tr) |
| Native | Uygulama içi doğal reklam | [Google Native Docs](https://developers.google.com/admob/android/native?hl=tr) |

## 6. AndroidManifest.xml

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXX~YYYYY" />
```

**Not:** APPLICATION_ID, AdsYield tarafından sağlanacaktır.

## 7. ProGuard / R8

Adapter AAR'ı kendi ProGuard kurallarını içerir, ek kural gerekmez.

## 8. Test Etme

- Google'ın test ad unit ID'lerini kullanarak test edin
- Ad Inspector ile adapter'ın doğru yüklendiğini doğrulayın: `MobileAds.openAdInspector(activity, ...)`
- Logcat'te `AdsYieldAdapter` tag'ini filtreleyin

## 9. Sorun Giderme

| Hata | Çözüm |
|------|-------|
| "No fill" | MCM ad unit'in aktif ve demand olduğundan emin olun |
| "Adapter not found" | AAR'ın doğru eklendiğinden ve class name'in doğru yazıldığından emin olun |
| "Ad failed to load" | Ad unit ID'nin doğru olduğundan emin olun |
| "Missing ad unit ID" | AdMob/GAM UI'da custom event parameter'ına ad unit ID yazıldığından emin olun |

---

# AdsYield Mediation Adapter - Integration Guide (English)

## 1. Requirements

- Android API Level 23+
- Google Mobile Ads SDK 24.7.0+
- AdMob or Google Ad Manager account

## 2. Gradle Setup

### Option 1: Via JitPack (Recommended)

Add JitPack repository to your project-level `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.bugranalci:AdsyieldMediationAdapter:1.0.0")
}
```

### Option 2: Via AAR file

```gradle
// Copy adsyield-mediation-adapter.aar to app/libs folder
dependencies {
    implementation(files("libs/adsyield-mediation-adapter.aar"))
    implementation("com.google.android.gms:play-services-ads:24.7.0")
}
```

**Note:** When using the AAR method, you need to add the Google Ads SDK separately. The JitPack method pulls it automatically.

> **Version Compatibility Note:** If your project already includes the Google Mobile Ads SDK dependency, make sure it is compatible with the version used by the adapter (24.7.0). In case of version conflicts, you can use Gradle resolution strategy.

## 3. Creating a Custom Event in AdMob

1. Sign in to your AdMob account: https://admob.google.com
2. From the left menu, select **Mediation > Waterfall sources**
3. Click **"Set up ad source"** > Select **"Add custom event"**
4. Add mapping:
   - **Mapping Name:** AdsYield
   - **Class Name:** `com.adsyield.mediation.adapter.AdsYieldAdapter`
   - **Parameter:** Ad Unit ID provided by AdsYield (e.g., `ca-app-pub-XXXXX/YYYYYY`)
5. Add this custom event to your mediation group and set the eCPM value

## 4. For GAM (Google Ad Manager)

Same steps apply for GAM:

- Follow **Yield Groups > Add yield partner > Custom event** path
- Class name and parameter are the same

## 5. Supported Ad Formats

| Format | Description | Documentation |
|--------|-------------|---------------|
| Banner | 320x50, 300x250, Adaptive Banner | [Google Banner Docs](https://developers.google.com/admob/android/banner) |
| Interstitial | Full-screen interstitial ad | [Google Interstitial Docs](https://developers.google.com/admob/android/interstitial) |
| Rewarded | Rewarded video ad | [Google Rewarded Docs](https://developers.google.com/admob/android/rewarded) |
| Rewarded Interstitial | Rewarded interstitial ad | [Google Rewarded Interstitial Docs](https://developers.google.com/admob/android/rewarded-interstitial) |
| Native | In-app native ad | [Google Native Docs](https://developers.google.com/admob/android/native) |

## 6. AndroidManifest.xml

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXX~YYYYY" />
```

**Note:** The APPLICATION_ID will be provided by AdsYield.

## 7. ProGuard / R8

The adapter AAR includes its own ProGuard rules; no additional rules are needed.

## 8. Testing

- Test using Google's test ad unit IDs
- Verify the adapter loads correctly with Ad Inspector: `MobileAds.openAdInspector(activity, ...)`
- Filter `AdsYieldAdapter` tag in Logcat

## 9. Troubleshooting

| Error | Solution |
|-------|----------|
| "No fill" | Make sure the MCM ad unit is active and has demand |
| "Adapter not found" | Make sure the AAR is correctly added and the class name is spelled correctly |
| "Ad failed to load" | Make sure the ad unit ID is correct |
| "Missing ad unit ID" | Make sure you've entered the ad unit ID in the custom event parameter in AdMob/GAM UI |
