/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.anvil)
    alias(libs.plugins.hilt)
    alias(libs.plugins.detekt)
    alias(libs.plugins.sortDependencies)
    alias(libs.plugins.spotless)
}

kotlin { jvmToolchain(libs.versions.jdk.get().toInt()) }

anvil { addOptionalAnnotations.set(true) }

android {
    namespace = "dev.whosnickdoglio.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.whosnickdoglio.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        javaCompileOptions.annotationProcessorOptions {
            arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    lint {
        htmlReport = false
        xmlReport = false
        textReport = true
        absolutePaths = false
        checkTestSources = true
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

spotless {
    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
}

dependencies {
    lintChecks(project(":lint:anvil"))
    lintChecks(project(":lint:dagger"))
    lintChecks(project(":lint:hilt"))

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)
}
