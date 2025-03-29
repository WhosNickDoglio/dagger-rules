/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins { alias(libs.plugins.kover) }

/*
This module just exists so Kover can create a merged report for code coverage.
 */
dependencies {
    kover(projects.lint.dagger)
    kover(projects.lint.anvil)
    kover(projects.lint.hilt)
}
