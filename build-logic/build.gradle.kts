/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

import com.diffplug.spotless.LineEnding

plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

kotlin { jvmToolchain(20) }

configure<com.diffplug.gradle.spotless.SpotlessExtension> {

    // https://github.com/diffplug/spotless/issues/1527
    // https://github.com/diffplug/spotless/issues/1644
    lineEndings = LineEnding.PLATFORM_NATIVE

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
    implementation(libs.spotless.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.android.gradle)
    implementation(libs.kover.gradle)
}
