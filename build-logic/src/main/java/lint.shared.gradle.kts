/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

import com.ncorti.ktfmt.gradle.KtfmtExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.gitlab.arturbosch.detekt")
    id("com.ncorti.ktfmt.gradle")
    id("com.android.lint")
    id("org.jetbrains.kotlinx.kover")
}

val catalog =
    extensions.findByType(VersionCatalogsExtension::class.java) ?: error("No Catalog found!")

val libs = catalog.named("libs")

lint {
    htmlReport = false
    xmlReport = false
    textReport = true
    absolutePaths = false
    checkTestSources = true
    warningsAsErrors = true
    baseline = file("lint-baseline.xml")
}

configure<KtfmtExtension> { kotlinLangStyle() }

configure<KotlinJvmProjectExtension> { jvmToolchain(11) }

tasks.withType<Detekt>().configureEach { jvmTarget = "11" }

tasks.withType<JavaCompile>().configureEach { options.release.set(11) }

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.SKIPPED, TestLogEvent.PASSED, TestLogEvent.FAILED)
        showStandardStreams = true
    }
    reports.html.required.set(false)
    reports.junitXml.required.set(false)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(true)
        jvmTarget.set(JvmTarget.JVM_11)
        // Lint forces Kotlin (regardless of what version the project uses), so this
        // forces a lower language level for now. Similar to `targetCompatibility` for Java.
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_7)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_7)
    }
}

dependencies {
    "compileOnly"(platform(libs.findLibrary("kotlin-bom").get()))
    "compileOnly"(libs.findBundle("lintApi").get())
    "testImplementation"(libs.findBundle("test").get())
    "testImplementation"(libs.findBundle("lintTest").get())
}
