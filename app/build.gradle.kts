import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "su.katso.synonym"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
    }

    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))

    implementation("androidx.core:core-ktx:1.0.1")

    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.google.android.material:material:1.0.0")

    implementation("com.bluelinelabs:conductor:2.1.5")
    implementation("com.bluelinelabs:conductor-support:2.1.5")
    implementation("com.bluelinelabs:conductor-archlifecycle:2.1.5")

    implementation("io.reactivex.rxjava2:rxjava:2.2.3")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")

    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
    implementation("com.squareup.okhttp3:okhttp:3.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")
    implementation("com.google.code.gson:gson:2.8.5")

    implementation("org.koin:koin-core:1.0.2")
    implementation("org.koin:koin-android:1.0.2")
    implementation("org.koin:koin-androidx-scope:1.0.2")
    implementation("com.aurelhubert:ahbottomnavigation:2.2.0")

    testImplementation("junit:junit:4.12")
}
