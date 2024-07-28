plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android") // Apply Kotlin Android plugin
    id("kotlin-kapt") // Apply Kotlin Annotation Processing plugin
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.tryexcel5"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tryexcel5"
        minSdk = 29
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)
    implementation (libs.poi)
    implementation (libs.poi.ooxml)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.xmlbeans)
    implementation (libs.commons.collections4)
    implementation (libs.androidx.work.runtime.ktx)
    implementation (libs.kotlinx.coroutines.android)


    implementation (libs.androidx.room.ktx)
    implementation (libs.kotlinx.coroutines.android.v171)

}