/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import dev.whosnickdoglio.hilt.HILT_ANDROID_APP
import org.junit.Test

class MissingHiltAndroidAppAnnotationDetectorTest {
    @Test
    fun `java application class without @HiltAndroidApp triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    package android.app
                    class Application
                """,
                )
                    .indented(),
                TestFiles.java(
                    """
                import android.app.Application;

                class MyApplication extends Application {}
            """,
                )
                    .indented(),
            )
            .issues(MissingHiltAndroidAppAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyApplication.java:3: Error: Application subclasses need @HiltAndroidApp [MissingHiltAndroidAppAnnotation]
                    class MyApplication extends Application {}
                          ~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent(),
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyApplication.java line 3: Add HiltAndroidApp annotation:
                @@ -3 +3
                - class MyApplication extends Application {}
                @@ -4 +3
                + class @dagger.hilt.android.HiltAndroidApp
                + MyApplication extends Application {}
            """
                    .trimIndent(),
            )
    }

    @Test
    fun `java application class with @HiltAndroidApp does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    package android.app
                    class Application
                """,
                )
                    .indented(),
                TestFiles.java(
                    """
                import android.app.Application;
                import $HILT_ANDROID_APP;

                @${HILT_ANDROID_APP.substringAfterLast(".")}
                class MyApplication extends Application {}
            """,
                )
                    .indented(),
            )
            .issues(MissingHiltAndroidAppAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin application class without @HiltAndroidApp triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    package android.app
                    class Application
                """,
                )
                    .indented(),
                TestFiles.kotlin(
                    """
                import android.app.Application

                class MyApplication : Application
            """,
                )
                    .indented(),
            )
            .issues(MissingHiltAndroidAppAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyApplication.kt:3: Error: Application subclasses need @HiltAndroidApp [MissingHiltAndroidAppAnnotation]
                    class MyApplication : Application
                          ~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent(),
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                    Fix for src/MyApplication.kt line 3: Add HiltAndroidApp annotation:
                    @@ -3 +3
                    - class MyApplication : Application
                    @@ -4 +3
                    + class @dagger.hilt.android.HiltAndroidApp
                    + MyApplication : Application
                """
                    .trimIndent(),
            )
    }

    @Test
    fun `kotlin application class with @HiltAndroidApp does not triggers error`() {
        TestLintTask.lint()
            .files(
                *hiltAnnotations,
                TestFiles.kotlin(
                    """
                    package android.app
                    class Application
                """,
                )
                    .indented(),
                TestFiles.kotlin(
                    """
                import android.app.Application
                import $HILT_ANDROID_APP

                @${HILT_ANDROID_APP.substringAfterLast(".")}
                class MyApplication : Application
            """,
                )
                    .indented(),
            )
            .issues(MissingHiltAndroidAppAnnotationDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
