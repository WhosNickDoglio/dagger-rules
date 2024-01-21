/*
 * Copyright (C) 2023 Nicholas Doglio
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

kotlin { jvmToolchain(libs.versions.jdk.get().toInt()) }

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
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kover.gradle)
    implementation(libs.sortDependencies.gradle)
    implementation(libs.spotless.gradle)
}
