import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jdk.get().toInt())
}

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<KaptGenerateStubsTask>().configureEach {
    // TODO necessary until anvil supports something for K2 contribution merging
    compilerOptions {
        progressiveMode.set(false)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    // TODO necessary until anvil supports something for K2 contribution merging
    compilerOptions {
        progressiveMode.set(false)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
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
    coreLibraryDesugaring(libs.desugar)

    lintChecks(projects.lint.anvil)
    lintChecks(projects.lint.dagger)
    lintChecks(projects.lint.hilt)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)
}
