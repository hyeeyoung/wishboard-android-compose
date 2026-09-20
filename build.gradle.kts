buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.devtools.ksp) apply false
//    alias(libs.plugins.google.services) apply false
//    alias(libs.plugins.firebase.crashlytics.gradle) apply false
}
