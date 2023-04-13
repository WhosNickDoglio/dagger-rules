/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.detekt)
}

kotlin { jvmToolchain(11) }

ktfmt { kotlinLangStyle() }

dependencies {
    implementation(libs.ktfmt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.android.gradle)
    implementation(libs.kover.gradle)
}
