import java.io.File
import java.io.FileInputStream
import java.util.*

fun getLocalProperty(key: String): String {
    val prop = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }
    return prop.getProperty(key)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinserialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "tech.svehla.gratitudejournal"
    compileSdk = 37

    defaultConfig {
        applicationId = "tech.svehla.gratitudejournal"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "WEB_CLIENT_ID", getLocalProperty("webClientId"))
        buildConfigField("String", "GIPHY_API_KEY", getLocalProperty("giphyApiKey"))
    }

    signingConfigs {
        val keystorePropertiesFile = rootProject.file("certificates/keystore.properties")
        if (keystorePropertiesFile.exists()) {
            val keystoreProperties = Properties().apply{
                load(keystorePropertiesFile.inputStream())
            }

            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = rootProject.file("certificates/${keystoreProperties.getProperty("keystore")}")
                storePassword = keystoreProperties.getProperty("keystorePassword")
            }
        }

        create("release") {
            keyAlias = "release"
            keyPassword = "my release key password"
            storeFile = file("/home/miles/keystore.jks")
            storePassword = "my keystore password"
        }

        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
        }
    }

    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            signingConfig = signingConfigs.getByName("release")
//            buildConfigField("boolean", "DEV_MODE", "false")
//        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("boolean", "DEV_MODE", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        screenshotTests.create("screenshotTest") {
            engineVersion = libs.versions.screenshot.validation.get()
            targetVariants.add("debug")

            dependencies {
                implementation(libs.compose.tooling)
                implementation(libs.screenshot.validation.api)
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)
    debugImplementation(libs.compose.toolingpreview)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.playservices.auth)
    implementation(libs.playservices.coroutines)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // UI
    implementation(libs.giphy)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.ktor3)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // network
//    implementation(libs.ktor.client.android)
//    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
//    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)

    // logging
    implementation(libs.timber)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.turbine)
    testImplementation(libs.test.truth)
    testImplementation(libs.kotlinx.coroutines.test)
//    androidTestImplementation("androidx.test.ext:junit:$versions.junitExtVersion")
//    androidTestImplementation("androidx.test.espresso:espresso-core:$versions.espressoCoreVersion")
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$versions.composeVersion")
}