import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
    alias(libs.plugins.lint)
    alias(libs.plugins.sortDependencies)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get().toInt())
        vendor = JvmVendorSpec.AZUL
    }
}

gradlePlugin {
    plugins {
        register("lint") {
            id = "dev.whosnickdoglio.lint"
            implementationClass = "dev.whosnickdoglio.buildlogic.LintPlugin"
        }

        register("rules") {
            id = "dev.whosnickdoglio.rules"
            implementationClass = "dev.whosnickdoglio.buildlogic.RulesPlugin"
        }
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
    disable.add("GradleDependency")
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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors = true
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

dependencies {
    lintChecks(libs.androidx.gradle.lints)
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kover.gradle)
    implementation(libs.sortDependencies.gradle)
    implementation(libs.spotless.gradle)
}
