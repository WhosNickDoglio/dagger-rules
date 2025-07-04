/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    alias(libs.plugins.convention.kotlin)
    alias(libs.plugins.ksp)
}

convention { enableCodeCoverageWithKover() }

dependencies {
    implementation(projects.lint.annotationConstants)

    compileOnly(libs.autoService.annotations)
    compileOnly(libs.lint.api)

    testImplementation(testFixtures(projects.lint.annotationConstants))
    testImplementation(libs.bundles.lintTest)
    testImplementation(libs.bundles.test)

    ksp(libs.autoService.ksp)
}
