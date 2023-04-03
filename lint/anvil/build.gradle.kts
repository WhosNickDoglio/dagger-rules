/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins { id("lint.shared") }

dependencies {
    implementation(projects.lint.shared)
    testImplementation(projects.lint.testStubs)
}
