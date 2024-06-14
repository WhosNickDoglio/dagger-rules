/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    id("dev.whosnickdoglio.lint")
    alias(libs.plugins.ksp)
    alias(libs.plugins.publish)
}

dependencies {
    ksp(libs.autoService.ksp)

    implementation(projects.lint.shared)

    compileOnly(libs.autoService.annotations)

    testImplementation(projects.lint.testStubs)
}
