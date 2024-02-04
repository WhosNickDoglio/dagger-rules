/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic.configuration

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal fun Project.getVersionCatalog(catalogName: String = "libs"): VersionCatalog {
  val catalog = extensions.getByType(VersionCatalogsExtension::class.java)

  return catalog.named(catalogName)
}

internal fun Project.dependOnBuildLogicTask(taskName: String) {
  tasks.named(taskName).configure {
    it.dependsOn(gradle.includedBuild("build-logic").task(":$taskName"))
  }
}
