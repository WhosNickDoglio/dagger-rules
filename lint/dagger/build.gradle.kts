/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    id("lint.shared")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.lint.shared)
    ksp(libs.autoService.ksp)
    implementation(libs.autoService.annotations)
    testImplementation(projects.lint.testStubs)
}
