/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    id("dev.whosnickdoglio.lint")
    `java-test-fixtures`
}

dependencies {
    testFixturesApi(libs.lint.tests)
}
