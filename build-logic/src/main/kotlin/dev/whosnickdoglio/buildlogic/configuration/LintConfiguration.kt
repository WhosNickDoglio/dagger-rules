/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic.configuration

import com.android.build.api.dsl.Lint
import org.gradle.api.Project

internal fun Project.configureLint() {
    pluginManager.apply("com.android.lint")
    dependOnBuildLogicTask("lint")
    extensions.getByType(Lint::class.java).apply {
        htmlReport = false
        xmlReport = false
        textReport = true
        absolutePaths = false
        checkTestSources = true
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
    }
}
