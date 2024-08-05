plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.sibama2024ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sibama2024ai"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("org.json:json:20210307")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")

    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation(libs.play.services.location)
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}