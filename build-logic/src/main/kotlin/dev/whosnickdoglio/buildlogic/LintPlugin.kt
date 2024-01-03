/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic

import dev.whosnickdoglio.buildlogic.configuration.getVersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

class LintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply(RulesPlugin::class.java)
        val libs = target.getVersionCatalog()
        with(target.dependencies) {
            add("compileOnly", libs.findLibrary("lint-api").get())
            add("testImplementation", libs.findBundle("test").get())
            add("testImplementation", libs.findBundle("lintTest").get())
        }
    }
}
