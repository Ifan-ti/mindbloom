plugins {
    // PAKAI SALAH SATU CARA SAJA → di sini pakai alias dari version catalog
    alias(libs.plugins.android.application)
    // butuh karena Gemini client ditulis Kotlin (tetap bisa dipanggil dari Java)
    id("org.jetbrains.kotlin.android") version "1.9.24"
    // Google Services (Firebase)
}

android {
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    namespace = "com.project.mindbloom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.mindbloom"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true // ← Tambahkan jika perlu
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

    implementation ("com.google.android.gms:play-services-base:18.5.0")
    // --- AndroidX & UI ---
    implementation(libs.appcompat)
    implementation(libs.material) // dari version catalog

    // Existing dependencies
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    // Pusher
    implementation ("com.pusher:pusher-java-client:2.4.2")
    implementation("com.google.code.gson:gson:2.10.1")


    // Optional: Lifecycle (untuk LiveData)
    implementation ("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.2")

    // --- Navigasi ---
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // --- RecyclerView & Gson ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")

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

    // --- CROP FOTO ---
    implementation ("com.github.yalantis:ucrop:2.2.11")

    // --- GRAFIK MOOD ---
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("androidx.cardview:cardview:1.0.0 ")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")


    // OkHttp for API calls

}
