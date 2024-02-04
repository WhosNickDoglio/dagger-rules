/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.buildlogic.configuration

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

internal fun Project.configureTests() {
  tasks.withType(Test::class.java).configureEach { testConfig ->
    with(testConfig) {
      forkEvery = FORK_EVERY_TEST
      maxParallelForks =
        (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
      testLogging { loggingContainer ->
        with(loggingContainer) {
          exceptionFormat = TestExceptionFormat.FULL
          events = setOf(TestLogEvent.SKIPPED, TestLogEvent.PASSED, TestLogEvent.FAILED)
          showStandardStreams = true
        }
      }
      reports.html.required.set(false)
      reports.junitXml.required.set(false)
    }
  }
}

private const val FORK_EVERY_TEST = 100L
