/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    alias(libs.plugins.convention.kotlin)
    `java-test-fixtures`
}

dependencies { testFixturesApi(libs.lint.tests) }
