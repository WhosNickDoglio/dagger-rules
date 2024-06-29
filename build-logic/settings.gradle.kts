/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

rootProject.name = "build-logic"

pluginManagement {
    repositories {
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroup("com.google.testing.platform")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroup("com.google.testing.platform")
            }
        }
        mavenCentral()
    }

    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" }
