/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    id("dev.whosnickdoglio.lint")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.lint.annotationConstants)

    compileOnly(libs.autoService.annotations)

    testImplementation(testFixtures(projects.lint.annotationConstants))

    ksp(libs.autoService.ksp)
}
