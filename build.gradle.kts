/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.doctor)
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.lint) apply false
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.spotless) apply false
}

doctor {
    // https://github.com/runningcode/gradle-doctor/pull/258
    warnWhenNotUsingParallelGC = false
}

// https://docs.gradle.org/8.9/userguide/gradle_daemon.html#daemon_jvm_criteria
tasks.updateDaemonJvm.configure {
    languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    vendor = JvmVendorSpec.AZUL
}
