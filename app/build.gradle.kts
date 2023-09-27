import org.jetbrains.kotlin.konan.properties.Properties

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.hyeeyoung.wishboard"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.hyeeyoung.wishboard"
        minSdk = 24
        targetSdk = 33
        versionCode = 30
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val contentProviderAuthority = "${applicationId}.fileprovier"
        manifestPlaceholders["FILE_PROVIDER"] = contentProviderAuthority
        buildConfigField("String", "FILE_PROVIDER", "\"$contentProviderAuthority\"")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "BASE_URL", properties.getProperty("DEV_BASE_URL"))
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", properties.getProperty("PROD_BASE_URL"))
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
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
    kapt(libs.bundles.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.accompanist)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.network)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.app.update)
    implementation(libs.junit)
    implementation(libs.timber)

    coreLibraryDesugaring(libs.desugar)
    debugImplementation(libs.bundles.debug)
    testImplementation(libs.bundles.test)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.bundles.android.test)
}

kapt {
    correctErrorTypes = true
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
