/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

rootProject.name = "build-logic"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" }
