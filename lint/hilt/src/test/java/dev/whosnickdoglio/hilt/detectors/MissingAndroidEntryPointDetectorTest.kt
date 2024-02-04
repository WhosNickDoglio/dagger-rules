/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.hilt.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.hilt.ANDROID_ENTRY_POINT
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MissingAndroidEntryPointDetectorTest {
  @TestParameter(
    value =
      [
        "android.app.Activity",
        "android.app.Fragment",
        "android.app.Service",
        "android.content.ContentProvider",
        "android.content.BroadcastReceiver",
        "androidx.fragment.app.Fragment",
      ],
  )
  lateinit var androidEntryPoint: String

  @Test
  fun `java android component using field injection with @AndroidEntryPoint does not trigger error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    TestLintTask.lint()
      .files(
        javaxAnnotations,
        *hiltAnnotations,
        TestFiles.java(
          """
                package $classPackage;

                class $className {}

                """,
        )
          .indented(),
        TestFiles.java(
          """
                package androidx;

                import $androidEntryPoint;
                import javax.inject.Inject;
                import $ANDROID_ENTRY_POINT;

                @${ANDROID_ENTRY_POINT.substringAfterLast(".")}
                class AndroidX$className extends $className {
                    @Inject String myString;
                }
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expectClean()
      .expectErrorCount(0)
  }

  @Test
  fun `java android component using field injection without @AndroidEntryPoint triggers error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    fun expectedErrorMessage(): String {
      val errorHighlight = "AndroidX$className".map { "~" }.joinToString(separator = "")

      return """
                src/androidx/AndroidX$className.java:7: Error: This class needs to be annotated with @AndroidEntryPoint to use field injection with Hilt.

                See https://whosnickdoglio.dev/dagger-rules/rules/#android-components-should-be-annotated-with-androidentrypoint for more information. [MissingAndroidEntryPointAnnotation]
                class AndroidX$className extends $className {
                      $errorHighlight
                1 errors, 0 warnings
            """
    }

    fun expectedFixDiff(): String =
      """
            Fix for src/androidx/AndroidX$className.java line 7: Add AndroidEntryPoint annotation:
            @@ -7 +7
            - class AndroidX$className extends $className {
            + class @dagger.hilt.android.AndroidEntryPoint
            + AndroidX$className extends $className {
        """
        .trimIndent()

    TestLintTask.lint()
      .files(
        *hiltAnnotations,
        javaxAnnotations,
        TestFiles.java(
          """
                package $classPackage;

                class $className {}

                """,
        )
          .indented(),
        TestFiles.java(
          """
                package androidx;

                import $androidEntryPoint;
                import javax.inject.Inject;


                class AndroidX$className extends $className {
                    @Inject String myString;
                }
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expect(expectedErrorMessage())
      .expectErrorCount(1)
      .expectFixDiffs(expectedFixDiff())
  }

  @Test
  fun `kotlin android component using field injection with @AndroidEntryPoint does not trigger error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    TestLintTask.lint()
      .files(
        *hiltAnnotations,
        javaxAnnotations,
        TestFiles.kotlin(
          """
                package $classPackage

                class $className

                """,
        )
          .indented(),
        TestFiles.kotlin(
          """
                package androidx

                import $androidEntryPoint
                import javax.inject.Inject
                import $ANDROID_ENTRY_POINT

                @${ANDROID_ENTRY_POINT.substringAfterLast(".")}
                class AndroidX$className: $className {
                    @Inject lateinit var string: String
                }
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expectClean()
      .expectErrorCount(0)
  }

  @Test
  fun `kotlin android component using field injection without @AndroidEntryPoint triggers error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    fun expectedErrorMessage(): String {
      val errorHighlight = "AndroidX$className".map { "~" }.joinToString(separator = "")

      return """
                src/androidx/AndroidX$className.kt:6: Error: This class needs to be annotated with @AndroidEntryPoint to use field injection with Hilt.

                See https://whosnickdoglio.dev/dagger-rules/rules/#android-components-should-be-annotated-with-androidentrypoint for more information. [MissingAndroidEntryPointAnnotation]
                class AndroidX$className : $className {
                      $errorHighlight
                1 errors, 0 warnings
            """
    }

    fun expectedFixDiff(): String =
      """
                Fix for src/androidx/AndroidX$className.kt line 6: Add AndroidEntryPoint annotation:
                @@ -6 +6
                - class AndroidX$className : $className {
                + class @dagger.hilt.android.AndroidEntryPoint
                + AndroidX$className : $className {
            """
        .trimIndent()

    TestLintTask.lint()
      .files(
        *hiltAnnotations,
        javaxAnnotations,
        TestFiles.kotlin(
          """
                package $classPackage

                class $className

                """,
        )
          .indented(),
        TestFiles.kotlin(
          """
                package androidx

                import javax.inject.Inject
                import $androidEntryPoint

                class AndroidX$className : $className {
                    @Inject
                    lateinit var something: String
                }
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expect(expectedErrorMessage())
      .expectErrorCount(1)
      .expectFixDiffs(expectedFixDiff())
  }

  @Test
  fun `kotlin android component not using field injection without @AndroidEntryPoint does not trigger error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    TestLintTask.lint()
      .files(
        *hiltAnnotations,
        TestFiles.kotlin(
          """
                package $classPackage

                class $className

                """,
        )
          .indented(),
        TestFiles.kotlin(
          """
                package androidx

                class AndroidX$className : $className
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expectClean()
      .expectErrorCount(0)
  }

  @Test
  fun `java android component not using field injection without @AndroidEntryPoint does not trigger error`() {
    val classPackage = androidEntryPoint.substringBeforeLast(".")
    val className = androidEntryPoint.substringAfterLast(".")

    TestLintTask.lint()
      .files(
        *hiltAnnotations,
        TestFiles.java(
          """
                package $classPackage;

                class $className {}

                """,
        )
          .indented(),
        TestFiles.java(
          """
                package androidx;

                class AndroidX$className extends $className {}
                    """,
        )
          .indented(),
      )
      .issues(MissingAndroidEntryPointDetector.ISSUE)
      .run()
      .expectClean()
      .expectErrorCount(0)
  }
}
