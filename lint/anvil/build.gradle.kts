/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.convention.kotlin)
    alias(libs.plugins.ksp)
}

convention { enableCodeCoverageWithKover() }

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
    }
}

dependencies {
    implementation(projects.lint.annotationConstants)

    compileOnly(libs.autoService.annotations)
    compileOnly(libs.lint.api)

    testImplementation(testFixtures(projects.lint.annotationConstants))
    testImplementation(libs.bundles.lintTest)
    testImplementation(libs.bundles.test)

    ksp(libs.autoService.ksp)
}
