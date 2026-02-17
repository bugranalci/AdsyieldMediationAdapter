plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.adsyield.mediation.adapter"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.play.services.ads)
}

// JitPack / Maven Publish konfigürasyonu
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.adsyield"
                artifactId = "mediation-adapter"
                version = "1.0.0"

                pom {
                    name.set("AdsYield Mediation Adapter")
                    description.set("AdsYield Mediation Adapter for Google AdMob and Google Ad Manager")
                    url.set("https://github.com/AdsYield/mediation-adapter-android")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
}
