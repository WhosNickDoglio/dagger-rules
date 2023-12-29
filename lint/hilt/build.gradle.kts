/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    id("lint.shared")
    alias(libs.plugins.ksp)
}

dependencies {
    ksp(libs.autoService.ksp)

    compileOnly(libs.autoService.annotations)
    implementation(projects.lint.shared)

    testImplementation(projects.lint.testStubs)
}
