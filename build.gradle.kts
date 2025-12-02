// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
// build.gradle (Project: MindBloom)
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.0") // sesuaikan versi
        classpath ("com.google.gms:google-services:4.4.0") // ← Tambahkan ini
    }
}

allprojects {

}

