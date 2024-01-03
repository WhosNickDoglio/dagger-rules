/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic

import dev.whosnickdoglio.buildlogic.configuration.configureJvm
import dev.whosnickdoglio.buildlogic.configuration.configureLint
import dev.whosnickdoglio.buildlogic.configuration.configureSpotless
import dev.whosnickdoglio.buildlogic.configuration.configureTests
import dev.whosnickdoglio.buildlogic.configuration.getVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

class RulesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.getVersionCatalog()
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            pluginManager.apply("org.jetbrains.kotlinx.kover")
            pluginManager.apply("com.squareup.sort-dependencies")
            configureJvm(libs.findVersion("jdk").get().requiredVersion.toInt())
            configureLint()
            configureSpotless(libs.findVersion("ktfmt").get().requiredVersion)
            configureTests()
        }
    }
}
