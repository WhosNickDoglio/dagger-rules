/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic.configuration

import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureJvm(toolchainVersion: Int) {
  extensions.getByType(KotlinJvmProjectExtension::class.java).jvmToolchain { toolchain ->
    toolchain.languageVersion.set(JavaLanguageVersion.of(toolchainVersion))
    toolchain.vendor.set(JvmVendorSpec.AZUL)
  }
  tasks.configureKotlin()
  tasks.configureJava()
}

internal fun TaskContainer.configureKotlin() {
  withType(KotlinCompile::class.java).configureEach { kotlinCompile ->
    kotlinCompile.compilerOptions {
      allWarningsAsErrors.set(true)
      jvmTarget.set(JvmTarget.JVM_11)
      // Lint forces Kotlin (regardless of what version the project uses), so this
      // forces a lower language level for now. Similar to `targetCompatibility` for Java.
      apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_7)
      languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_7)
    }
  }
}

internal fun TaskContainer.configureJava() {
  withType(JavaCompile::class.java).configureEach { javaCompile ->
    javaCompile.options.release.set(JAVA_RELEASE_OPTIONS)
  }
}

private const val JAVA_RELEASE_OPTIONS = 11
