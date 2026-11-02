import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Release key lives outside the repo; keystore.path (git-ignored) points to its keystore.properties
val keystoreProps = rootProject.file("keystore.path").takeIf { it.exists() }
    ?.let { file(it.readText().trim()) }?.takeIf { it.exists() }
    ?.let { f -> Properties().apply { f.inputStream().use { load(it) } } }

android {
    signingConfigs {
        if (keystoreProps != null) create("release") {
            storeFile = file(keystoreProps.getProperty("storeFile"))
            storePassword = keystoreProps.getProperty("storePassword")
            keyAlias = keystoreProps.getProperty("keyAlias")
            keyPassword = keystoreProps.getProperty("keyPassword")
        }
    }
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
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
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
