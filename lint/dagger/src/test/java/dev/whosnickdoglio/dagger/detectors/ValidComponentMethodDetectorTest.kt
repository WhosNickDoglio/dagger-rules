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
class ValidComponentMethodDetectorTest {

    @TestParameter(value = [SUBCOMPONENT, COMPONENT])
    lateinit var component: String

    @Test
    fun `kotlin component has valid provision method does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                package com.test.android

                import $component

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     fun myThing(): String
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `java component has valid provision method does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                    """
                package com.test.android;

                import $component;

                 @${component.substringAfterLast(".")}
                 interface MyModule {
                        String myString();
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin component with valid member injection method does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                package com.test.android

                import $component

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     fun inject(target: String)
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `java component with valid member injection method does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                    """
                package com.test.android;

                import $component;

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     void inject(String target);
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin component with invalid member injection methods triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                package com.test.android

                import $component

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     fun injectWithTwoParams(target: String, otherTarget: Int)
                     fun injectWithReturnType(target: String): String
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/AppComponent.kt:7: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     fun injectWithTwoParams(target: String, otherTarget: Int)
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/com/test/android/AppComponent.kt:8: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     fun injectWithReturnType(target: String): String
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                2 errors, 0 warnings
            """
                    .trimIndent(),
            )
            .expectErrorCount(2)
    }

    @Test
    fun `java component with invalid member injection methods triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                    """
                package com.test.android;

                import $component;

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     void injectWithTwoParams(String target, Int otherTarget);
                     String injectWithReturnType(String target);
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/AppComponent.java:7: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     void injectWithTwoParams(String target, Int otherTarget);
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/com/test/android/AppComponent.java:8: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     String injectWithReturnType(String target);
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                2 errors, 0 warnings
            """
                    .trimIndent(),
            )
            .expectErrorCount(2)
    }

    @Test
    fun `kotlin component with invalid provision methods triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                    """
                package com.test.android

                import $component

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     fun myThingWithParameter(otherThing: String): String
                     fun myThingWithNoReturnType()
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/AppComponent.kt:7: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     fun myThingWithParameter(otherThing: String): String
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/com/test/android/AppComponent.kt:8: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     fun myThingWithNoReturnType()
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                2 errors, 0 warnings
            """
                    .trimIndent(),
            )
            .expectErrorCount(2)
    }

    @Test
    fun `java component with invalid provision methods triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                    """
                package com.test.android;

                import $component;

                 @${component.substringAfterLast(".")}
                 interface AppComponent {
                     String myThingWithParameter(String otherThing);
                     void myThingWithNoReturnType();
                }
                """,
                )
                    .indented(),
            )
            .issues(ValidComponentMethodDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/test/android/AppComponent.java:7: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     String myThingWithParameter(String otherThing);
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                src/com/test/android/AppComponent.java:8: Error: Methods in a @Component interface either need to take a single parameter with no return type (member injection methods) or take no parameters and return a injected or provided type (provision methods), anything else will create a compile time error.See https://whosnickdoglio.dev/dagger-rules/rules/#valid-component-methods for more information. [ValidComponentMethod]
                     void myThingWithNoReturnType();
                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                2 errors, 0 warnings
            """
                    .trimIndent(),
            )
            .expectErrorCount(2)
    }
}
