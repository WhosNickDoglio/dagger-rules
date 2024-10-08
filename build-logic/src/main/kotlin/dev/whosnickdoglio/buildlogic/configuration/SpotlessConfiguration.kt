/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic.configuration

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project

internal fun Project.configureSpotless(ktfmtVersion: String) {
    pluginManager.apply("com.diffplug.spotless")
    dependOnBuildLogicTask("spotlessCheck")
    dependOnBuildLogicTask("spotlessApply")
    extensions.getByType(SpotlessExtension::class.java).apply {
        format("misc") { formatExtension ->
            with(formatExtension) {
                target("*.md", ".gitignore")
                trimTrailingWhitespace()
                endWithNewline()
            }
        }

        kotlin { kotlinExtension ->
            with(kotlinExtension) {
                ktfmt(ktfmtVersion).kotlinlangStyle()
                trimTrailingWhitespace()
                endWithNewline()
                licenseHeaderFile(file("$rootDir/spotless/spotless.kt"))
            }
        }
        kotlinGradle { kotlinGradleExtension ->
            with(kotlinGradleExtension) {
                ktfmt(ktfmtVersion).kotlinlangStyle()
                trimTrailingWhitespace()
                endWithNewline()
                licenseHeaderFile(
                    file("$rootDir/spotless/spotless.kt"),
                    "(import|plugins|buildscript|dependencies|pluginManagement)",
                )
            }
        }
    }
}
