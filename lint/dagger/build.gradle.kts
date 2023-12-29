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

    implementation(projects.lint.shared)

    compileOnly(libs.autoService.annotations)

    testImplementation(projects.lint.testStubs)
}
