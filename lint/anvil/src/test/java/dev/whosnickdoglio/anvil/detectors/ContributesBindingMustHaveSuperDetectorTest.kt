/*
 * Copyright (C) 2024 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_TO
import dev.whosnickdoglio.stubs.anvilAnnotations
import dev.whosnickdoglio.stubs.daggerAnnotations
import dev.whosnickdoglio.stubs.javaxAnnotations
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
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    interface Authenticator

                    @${annotation.substringAfterLast(".")}(AppScope::class)
                    class AuthenticatorImpl @Inject constructor(): Authenticator
                    """
                    )
                    .indented(),
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
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    @${annotation.substringAfterLast(".")}(AppScope::class)
                    class AuthenticatorImpl @Inject constructor()
                    """
                    )
                    .indented(),
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_BINDING_NO_SUPER)
            .run()
            .expect(
                """
                src/AuthenticatorImpl.kt:5: Warning: The ContributesBinding annotation is used to bind concrete implementations to an interface/abstract they implement if there is no interface or abstract class to implement using @ContributesBinding will throw an error at compile time.  [ContributesBindingMustHaveSuper]
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
                    .indented(),
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_CONTRIBUTES_TO_INSTEAD_OF_BINDING)
            .run()
            .expect(
                """
                    src/MyModule.kt:4: Error: The ContributesTo annotation is used to contribute Dagger modules to the DI graph whereas the ContributesBinding annotation is used to bind specific classes to one of their super interfaces/abstract classes in the DI graph and would not work with a Dagger module. [UseContributesToInstead]
                    @${annotation.substringAfterLast(".")}
                    ~${annotation.substringAfterLast(".").map { "~" }.joinToString(separator = "")}
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(0)
            .expectFixDiffs(
                """
                    Fix for src/MyModule.kt line 4: Did you mean to use the `@ContributesTo` annotation?:
                    @@ -4 +4
                    - @${annotation.substringAfterLast(".")}
                    + @$CONTRIBUTES_TO
                    +
                """
                    .trimIndent()
            )
    }

    @Test
    fun `boundType = Any does not show an error despite no declared super`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    @${annotation.substringAfterLast(".")}(boundType = Any::class)
                    class AuthenticatorImpl @Inject constructor()
                    """
                    )
                    .indented(),
            )
            .issues(ContributesBindingMustHaveSuperDetector.ISSUE_BINDING_NO_SUPER)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }
}
