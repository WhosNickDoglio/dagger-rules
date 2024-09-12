/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic

import dev.whosnickdoglio.buildlogic.configuration.configureJvm
import dev.whosnickdoglio.buildlogic.configuration.configureLint
import dev.whosnickdoglio.buildlogic.configuration.configureSpotless
import dev.whosnickdoglio.buildlogic.configuration.configureTests
import dev.whosnickdoglio.buildlogic.configuration.dependOnBuildLogicTask
import dev.whosnickdoglio.buildlogic.configuration.getVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

class RulesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.getVersionCatalog()
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlinx.kover")

            pluginManager.apply("io.gitlab.arturbosch.detekt")
            dependOnBuildLogicTask("detekt")
            dependOnBuildLogicTask("detektMain")
            dependOnBuildLogicTask("detektTest")

            pluginManager.apply("com.autonomousapps.dependency-analysis")

            pluginManager.apply("com.squareup.sort-dependencies")
            dependOnBuildLogicTask("sortDependencies")
            dependOnBuildLogicTask("checkSortDependencies")

            configureJvm(libs.findVersion("jdk").get().requiredVersion.toInt())
            configureLint()
            configureSpotless(libs.findVersion("ktlint").get().requiredVersion)
            configureTests()
        }
    }
}
