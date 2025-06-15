plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}
kapt {
    correctErrorTypes = true
}

apply {
    from("gradle-mvn-push.gradle")
}

android {
    namespace = "com.its.baseapp"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    api("androidx.activity:activity-ktx:1.9.0")
    api("androidx.fragment:fragment-ktx:1.7.0")

    api("com.intuit.sdp:sdp-android:1.1.0")
    api("com.intuit.ssp:ssp-android:1.1.0")

    api ("com.google.code.gson:gson:2.10.1")
    api("com.tbuonomo:dotsindicator:5.0")
    api("com.github.bumptech.glide:glide:4.16.0")

    api("androidx.room:room-runtime:2.6.1")
    api("androidx.room:room-ktx:2.6.1")
//    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    api("io.reactivex.rxjava3:rxandroid:3.0.2")
    api("io.reactivex.rxjava3:rxjava:3.1.8")
    api("com.squareup.retrofit2:retrofit:2.11.0")
    api("com.squareup.retrofit2:converter-gson:2.11.0")
    api("com.google.code.gson:gson:2.10.1")
    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
    api("androidx.browser:browser:1.8.0")
}