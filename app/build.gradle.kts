plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "fr.amandio.lanterna"
        minSdk = 26
        // Play Protect blocks sideloaded apps that target an old API level
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lint {
        abortOnError = false
    }
    namespace = "fr.amandio.lanterna"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
