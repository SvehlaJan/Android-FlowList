plugins {
    alias(libs.plugins.dependencyanalysis)
    alias(libs.plugins.kotlinserialization)
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.google.services)
    }
}