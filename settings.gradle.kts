enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Paper"

include(
    ":app"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {

    defaultLibrariesExtensionName.set("libs")

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        maven { url = java.net.URI("https://jitpack.io") }

        // org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3
        //  Required by: androidx.compose.runtime:runtime:1.0.0-beta01
        maven {
            url = java.net.URI("https://kotlin.bintray.com/kotlinx")
        }
    }
}
include(":editor")
