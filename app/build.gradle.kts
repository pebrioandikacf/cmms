plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ptpn.cmms"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ptpn.cmms"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {
    // BOM untuk Compose
    implementation(platform(libs.androidx.compose.bom))

    // Core & Activity (gunakan activity-compose untuk Compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // Material (Compose) dan Material3
    implementation("androidx.compose.material:material")                         // Material (compose)
    implementation("androidx.compose.material:material-icons-extended")         // Material icons (compose)
    implementation("androidx.compose.material3:material3:${libs.versions.material3.get()}") // Material3 (sudah kamu pakai)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Coil untuk image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // ZXing (QR)
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // AndroidX / Material legacy libs kamu
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Ads / lainnya (tetap di sini jika memang perlu)
    implementation(libs.ads.mobile.sdk)

    // Opsional tapi direkomendasikan untuk nanti (ViewModel & Coroutines)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}


