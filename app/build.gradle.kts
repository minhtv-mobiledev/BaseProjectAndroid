plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.minhtv.base"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.minhtv.base"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "ENABLE_LOGGING", "false")
        }
        flavorDimensions += "environment"

        productFlavors {
            create("development_creative") {
                dimension = "environment"
            }
            create("development") {
                dimension = "environment"
                buildConfigField("Boolean", "IS_REALEASED", "false")
            }
            create("staging") {
                dimension = "environment"
                buildConfigField("Boolean", "IS_REALEASED", "false")
            }
            create("production") {
                dimension = "environment"
                buildConfigField("Boolean", "IS_REALEASED", "true")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}
// 🟡 Thêm block này bên ngoài android { }, cùng cấp:
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {
    implementation(project(":baseApp"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.google.dagger:hilt-android:2.51.1")
    ksp ("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("io.coil-kt:coil:2.6.0")
    implementation("com.github.skydoves:colorpickerview:2.3.0")

//    implementation ("com.github.QuadFlask:colorpicker:0.0.13")

    implementation ("org.jcodec:jcodec:0.2.5")
    implementation ("org.jcodec:jcodec-android:0.2.5")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("com.airbnb.android:lottie:6.0.0")
    // Room Dependencies (Kotlin DSL)
    implementation("androidx.room:room-runtime:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    implementation("androidx.room:room-ktx:2.7.1") // Optional for coroutines support

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.1")

    implementation("com.google.flatbuffers:flatbuffers-java:25.2.10") //flatbuffer

    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    implementation ("com.google.dagger:hilt-android:2.51.1")
    ksp ("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("com.github.bumptech.glide:glide:4.16.0") // Use the latest version!
    ksp ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28") // Use the latest version!

    val camerax_version = "1.3.0" // Use the latest version!
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // Optional, if you want to use Viewfinder (preview)
    implementation("androidx.camera:camera-view:${camerax_version}")

    val billing_version = "7.1.1"

    implementation("com.android.billingclient:billing:$billing_version")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Firebase Remote Config
    implementation("com.google.firebase:firebase-config-ktx")

    // Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.android.play:review-ktx:2.0.2")

    implementation  ("com.google.ads.mediation:applovin:13.2.0.1")
    implementation  ("com.google.ads.mediation:fyber:8.3.7.0")
    implementation  ("com.google.ads.mediation:inmobi:10.8.3.0")
    implementation  ("com.google.ads.mediation:ironsource:8.7.0.1")
    implementation  ("com.google.ads.mediation:vungle:7.5.0.0")
    implementation  ("com.google.ads.mediation:facebook:6.20.0.0")
    implementation  ("com.google.ads.mediation:mintegral:16.9.71.0")
    implementation  ("com.google.ads.mediation:pangle:6.5.0.9.0")
    implementation  ("com.unity3d.ads:unity-ads:4.14.2")
    implementation  ("com.google.ads.mediation:unity:4.14.2.0")
    implementation ("com.yandex.ads.adapter:admob-mobileads:7.12.2.0")

}