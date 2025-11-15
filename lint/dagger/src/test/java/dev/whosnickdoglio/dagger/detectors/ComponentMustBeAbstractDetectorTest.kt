// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import dev.whosnickdoglio.stubs.anvilAnnotations
import dev.whosnickdoglio.stubs.daggerAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ComponentMustBeAbstractDetectorTest {
    private class ComponentMustBeAbstractTestParameterValueProvider :
        TestParameterValuesProvider() {
        override fun provideValues(context: Context?): List<*> =
            ComponentMustBeAbstractDetector.componentAnnotations.toList()
    }

    @TestParameter(valuesProvider = ComponentMustBeAbstractTestParameterValueProvider::class)
    lateinit var componentAnnotation: String

    @Test
    fun `kotlin abstract class component does not show error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                anvilAnnotations,
                TestFiles.kotlin(
                        """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    abstract class MyComponent
                """
                    )
                    .indented(),
            )
            .issues(ComponentMustBeAbstractDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `java abstract class component does not show error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                anvilAnnotations,
                TestFiles.java(
                        """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    abstract class MyComponent {}
                """
                    )
                    .indented(),
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
                anvilAnnotations,
                TestFiles.kotlin(
                        """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    interface MyComponent
                """
                    )
                    .indented(),
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
                anvilAnnotations,
                TestFiles.java(
                        """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    interface MyComponent {}
                """
                    )
                    .indented(),
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
                anvilAnnotations,
                TestFiles.java(
                        """
                    import $componentAnnotation;

                    @${componentAnnotation.substringAfterLast(".")}
                    class MyComponent {}
                """
                    )
                    .indented(),
            )
            .issues(ComponentMustBeAbstractDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyComponent.java:3: Error: A type annotated with @Component or @Subcomponent need to be abstract.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-annotated-with-component-must-be-abstract for more information. [ComponentMustBeAbstract]
                    @${componentAnnotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyComponent.java line 3: Make MyComponent an interface:
                @@ -4 +4
                - class MyComponent {}
                + interface MyComponent {}
                """
            )
    }

    @Test
    fun `kotlin class component shows an error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                anvilAnnotations,
                TestFiles.kotlin(
                        """
                    import $componentAnnotation

                    @${componentAnnotation.substringAfterLast(".")}
                    class MyComponent
                """
                    )
                    .indented(),
            )
            .issues(ComponentMustBeAbstractDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyComponent.kt:3: Error: A type annotated with @Component or @Subcomponent need to be abstract.

                    See https://whosnickdoglio.dev/dagger-rules/rules/#classes-annotated-with-component-must-be-abstract for more information. [ComponentMustBeAbstract]
                    @${componentAnnotation.substringAfterLast(".")}
                    ^
                    1 errors, 0 warnings
                """
            )
            .expectErrorCount(1)
            .expectFixDiffs(
                """
                Fix for src/MyComponent.kt line 3: Make MyComponent an interface:
                @@ -4 +4
                - class MyComponent
                + interface MyComponent
                """
            )
    }
}
