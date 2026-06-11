// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

buildscript {
    dependencies {
        // https://github.com/autonomousapps/dependency-analysis-gradle-plugin/issues/1661
        classpath(libs.kotlin.metadata)
    }
}

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.doctor)
    alias(libs.plugins.auto) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.lint) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.ktfmt) apply false
}

doctor {
    javaHome {
        failOnError = false 
    }
}

// https://docs.gradle.org/8.9/userguide/gradle_daemon.html#daemon_jvm_criteria
tasks.updateDaemonJvm.configure {
    languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    vendor = JvmVendorSpec.AZUL
}
