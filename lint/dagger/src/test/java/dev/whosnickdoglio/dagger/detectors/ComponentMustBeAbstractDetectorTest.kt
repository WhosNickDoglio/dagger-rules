/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.lint.shared.COMPONENT
import dev.whosnickdoglio.lint.shared.SUBCOMPONENT
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ComponentMustBeAbstractDetectorTest {
  @TestParameter(value = [COMPONENT, SUBCOMPONENT])
  lateinit var componentAnnotation: String

  @Test
  fun `kotlin abstract class component does not show error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.kotlin(
          """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    abstract class MyComponent
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expectClean()
  }

  @Test
  fun `java kotlin abstract class component does not show error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.java(
          """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    abstract class MyComponent {}
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expectClean()
  }

  @Test
  fun `kotlin interface component does not show error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.kotlin(
          """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    interface MyComponent
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expectClean()
  }

  @Test
  fun `java interface component does not show error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.java(
          """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    interface MyComponent {}
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expectClean()
  }

  @Test
  fun `java class component shows an error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.java(
          """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    class MyComponent {}
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expect(
        """
                    src/MyComponent.java:4: Error: A type annotated with @Component or @Subcomponent need to be abstract.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-annotated-with-component-must-be-abstract for more information. [ComponentMustBeAbstract]
                    class MyComponent {}
                          ~~~~~~~~~~~
                    1 errors, 0 warnings
                """
          .trimIndent(),
      )
      .expectErrorCount(1)
  }

  @Test
  fun `kotlin class component shows an error`() {
    TestLintTask.lint()
      .files(
        daggerAnnotations,
        TestFiles.kotlin(
          """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    class MyComponent
                """
            .trimIndent(),
        ),
      )
      .issues(ComponentMustBeAbstractDetector.ISSUE)
      .run()
      .expect(
        """
                    src/MyComponent.kt:4: Error: A type annotated with @Component or @Subcomponent need to be abstract.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-annotated-with-component-must-be-abstract for more information. [ComponentMustBeAbstract]
                    class MyComponent
                          ~~~~~~~~~~~
                    1 errors, 0 warnings
                """
          .trimIndent(),
      )
      .expectErrorCount(1)
  }
}
