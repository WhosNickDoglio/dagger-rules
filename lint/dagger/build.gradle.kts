// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT
plugins {
    alias(libs.plugins.convention.kotlin)
    alias(libs.plugins.auto)
    alias(libs.plugins.publish)
}

convention { codeCoverage() }

dependencies {
    implementation(projects.lint.annotationConstants)

    compileOnly(libs.lint.api)

    testImplementation(testFixtures(projects.lint.annotationConstants))
    testImplementation(libs.bundles.lintTest)
    testImplementation(libs.bundles.test)
}
