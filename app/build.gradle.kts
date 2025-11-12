plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aryan.digital_wallet_main"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aryan.digital_wallet_main"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Room Database
    implementation (libs.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)

// Material Design
    implementation (libs.material.v1110)

// ZXing
    implementation (libs.zxing.android.embedded)

// For ViewModel and LiveData
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

// Flexbox layout
    implementation (libs.flexbox)

// JSON handling
    implementation (libs.json)
    //lottie
    implementation (libs.lottie)
    // ML Kit Text Recognition
    implementation (libs.text.recognition)
    implementation (libs.itext7.core)
}