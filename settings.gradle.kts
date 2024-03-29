/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

rootProject.name = "dagger-rules"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

gradleEnterprise {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        tag(if (System.getenv("CI").isNullOrBlank()) "Local" else "CI")
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
