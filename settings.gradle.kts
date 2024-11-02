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
    id("com.gradle.develocity") version "3.18.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "2.0.2"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":kover-aggregate",
    ":lint:dagger",
    ":lint:anvil",
    ":lint:hilt",
    ":lint:annotation-constants",
    ":test-app",
)
