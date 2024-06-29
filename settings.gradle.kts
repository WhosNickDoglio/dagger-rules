/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

rootProject.name = "dagger-rules"

pluginManagement {
    includeBuild("build-logic")
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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
}

plugins {
    id("com.gradle.develocity") version "3.17.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        tag(if (providers.environmentVariable("CI").isPresent) "CI" else "Local")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":kover-aggregate",
    ":lint:dagger",
    ":lint:anvil",
    ":lint:hilt",
    ":lint:shared",
    ":lint:test-stubs",
    ":test-app",
)
