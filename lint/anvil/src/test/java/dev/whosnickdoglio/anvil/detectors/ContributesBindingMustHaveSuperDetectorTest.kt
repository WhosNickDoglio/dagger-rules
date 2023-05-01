/*
 * Copyright (C) 2023 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.injectAnnotation
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ContributesBindingMustHaveSuperDetectorTest {

    @TestParameter(value = [CONTRIBUTES_BINDING, CONTRIBUTES_MULTI_BINDING])
    lateinit var annotation: String

    @Test
    fun `class annotated with binding annotation with super shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    interface Authenticator

                    @${annotation.substringAfterLast(".")}
                    class AuthenticatorImpl @Inject constructor(): Authenticator
                    """
                    )
                    .indented()
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_BINDING_NO_SUPER)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `class annotated with binding annotation without super shows an error`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    class AuthenticatorImpl @Inject constructor()
                    """
                    )
                    .indented()
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_BINDING_NO_SUPER)
            .run()
            .expect(
                """
                src/AuthenticatorImpl.kt:5: Warning: Hello friend [ContributesBindingMustHaveSuper]
                class AuthenticatorImpl @Inject constructor()
                      ~~~~~~~~~~~~~~~~~
                0 errors, 1 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }
    @Test
    fun `dagger module annotated with binding annotation shows error`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import $annotation

                    @${annotation.substringAfterLast(".")}
                    @Module
                    class MyModule
                    """
                    )
                    .indented()
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING)
            .run()
            .expect(
                """
                src/MyModule.kt:6: Error: Hello friend [UseContributesToInstead]
                class MyModule
                      ~~~~~~~~
                1 errors, 0 warnings
            """
                    .trimIndent()
            )
            .expectWarningCount(0)
            .expectFixDiffs(
                """
                Fix for src/MyModule.kt line 6: Did you mean `@ContributesTo` annotation?:
                @@ -6 +6
                - class MyModule
                @@ -7 +6
                + class @com.squareup.anvil.annotations.ContributesTo
                + MyModule
            """
                    .trimIndent()
            )
    }
}
