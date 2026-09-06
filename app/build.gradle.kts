import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.Properties

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.dagger.hilt)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.hyeeyoung.wishboard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hyeeyoung.wishboard"
        minSdk = 24
        targetSdk = 36
        versionCode = 47
        versionName = "3.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val contentProviderAuthority = "${applicationId}.fileprovier"
        manifestPlaceholders["FILE_PROVIDER"] = contentProviderAuthority
        buildConfigField("String", "FILE_PROVIDER", "\"$contentProviderAuthority\"")
    }

    signingConfigs {
        create("release") {
            keyAlias = getProperty("KEY_ALIAS")
            keyPassword = getProperty("KEY_PASSWORD")
            storeFile = file(getProperty("KEYSTORE_PATH"))
            storePassword = getProperty("STORE_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("DEV_BASE_URL")}\"")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("PROD_BASE_URL")}\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.complier.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(libs.bundles.androidx)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.compose.material.three)

    implementation(libs.bundles.dagger.hilt)
    ksp(libs.bundles.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.accompanist.ui.controller)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.network)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.app.update)
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.bundles.paging)
    implementation("sh.calvin.reorderable:reorderable:3.0.0")
    coreLibraryDesugaring(libs.desugar)
    debugImplementation(libs.bundles.debug)
    testImplementation(libs.bundles.test)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.bundles.android.test)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("0.48.0").setEditorConfigPath("$rootDir/.editorconfig")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

fun getProperty(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key)
}