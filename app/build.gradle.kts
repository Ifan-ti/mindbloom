plugins {
    // PAKAI SALAH SATU CARA SAJA → di sini pakai alias dari version catalog
    alias(libs.plugins.android.application)

    // butuh karena Gemini client ditulis Kotlin (tetap bisa dipanggil dari Java)
    id("org.jetbrains.kotlin.android") version "1.9.24"

    // Google Services (Firebase)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.mindbloom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.mindbloom"
        minSdk = 29
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
        // Konsisten dengan toolchain-mu
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // --- AndroidX & UI ---
    implementation(libs.appcompat)
    implementation(libs.material) // dari version catalog

    // --- Firebase (pakai BoM, jadi JANGAN tulis versi di tiap artefak) ---
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // --- Navigasi ---
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // --- RecyclerView & Gson ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Retrofit & OkHttp ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Pakai BOM OkHttp → JANGAN tulis versi di artefak turunannya
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // --- Glide (untuk Java: pakai annotationProcessor) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // --- (Opsional) Coroutines kalau nanti dipakai ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- SVG ---
    implementation("com.caverock:androidsvg:1.4")

}
