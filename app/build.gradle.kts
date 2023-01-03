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
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "tech.svehla.gratitudejournal"

    defaultConfig {
        applicationId = "tech.svehla.gratitudejournal"
        compileSdk = 33
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true",
                    "dagger.hilt.disableModulesHaveInstallInCheck" to "true"
                )
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.RequiresOptIn",
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            exclude("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.corektx)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)
    debugImplementation(libs.compose.toolingpreview)
    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$versions.lifecycleVersion")
//    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha03")
//    debugImplementation("androidx.compose.ui:ui-test-manifest:$versions.composeVersion")

    implementation(libs.playservices.auth)
    implementation(libs.playservices.coroutines)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // UI
    implementation(libs.giphy)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

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