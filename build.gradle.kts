/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.lint) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.doctor)
    alias(libs.plugins.dependencyAnalysis)
}
