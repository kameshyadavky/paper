plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

val useReleaseKeystore = rootProject.file("release/app-release.jks").exists()

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.beetlestance.paper"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName(if (useReleaseKeystore) "release" else "debug")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        // Disable lintVital. Not needed since lint is run on CI
        checkReleaseBuilds = false
        // Allow lint to check dependencies
        checkDependencies = true
        // Ignore any tests
        ignoreTestSources = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
    }

    packagingOptions {
        // A failure occurred while executing com.android.build.gradle.internal.tasks.MergeJavaResWorkAction
        // https://github.com/Kotlin/kotlinx.coroutines/issues/2023
        resources.excludes.addAll(listOf("META-INF/AL2.0", "META-INF/LGPL2.1"))
    }
}

dependencies {
    implementation(projects.editor)

    implementation(libs.androidx.palette)

    // Testing
    testImplementation(libs.test.junit.core)
    testImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)

    // Hilt
    implementation(libs.google.hilt.android)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.google.hilt.compiler)


    // Hilt Nav ViewModel
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose ViewModel Extensions
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    // Material Design
    implementation(libs.google.material.core)
    implementation(libs.google.material.compose.theme.adapter)

    implementation(libs.activityCompose)

    // Compose
    implementation(libs.bundles.androidx.compose)

    implementation(libs.bundles.google.accompanist)

    implementation(libs.coil.compose)

    implementation(libs.gson)
}
