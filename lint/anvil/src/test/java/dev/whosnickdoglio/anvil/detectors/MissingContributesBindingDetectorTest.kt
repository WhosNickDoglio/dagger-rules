/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.lint.annotations.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.stubs.anvilAnnotations
import dev.whosnickdoglio.stubs.javaxAnnotations
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class MissingContributesBindingDetectorTest {
    @Test
    fun `kotlin class with super using dagger is missing @ContributesBinding annotation shows warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface Authenticator

                    class AuthenticatorImpl @Inject constructor(): Authenticator
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expect(
                """
                    src/Authenticator.kt:5: Warning: Contribute this binding to the Dagger graph using an Anvil annotation [MissingContributesBindingAnnotation]
                    class AuthenticatorImpl @Inject constructor(): Authenticator
                          ~~~~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
            )
            .expectWarningCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/Authenticator.kt line 5: Add @ContributesBinding annotation:
                    @@ -5 +5
                    + @com.squareup.anvil.annotations.ContributesBinding
                    Autofix for src/Authenticator.kt line 5: Add @ContributesMultibinding annotation:
                    @@ -5 +5
                    + @com.squareup.anvil.annotations.ContributesMultibinding
                """
            )
    }

    @Test
    fun `kotlin class without super using dagger and no @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    class AuthenticatorImpl @Inject constructor()
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin class with super using dagger with @ContributesBinding annotation shows no warning`(
        @TestParameter(value = [CONTRIBUTES_BINDING, CONTRIBUTES_MULTI_BINDING]) annotation: String
    ) {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    interface Authenticator

                    @${annotation.substringAfterLast(".")}
                    class AuthenticatorImpl @Inject constructor(): Authenticator
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin class without @ContributesBinding annotation but only super takes generics shows no warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface JsonAdapter<T>

                    class MyJsonAdapter @Inject constructor(): JsonAdapter<String>
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin class without @ContributesBinding annotation with a multiple supers shows a warning`() {
        TestLintTask.lint()
            .files(
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface JsonAdapter<T>
                    interface CustomAdapter

                    class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expect(
                """
                src/JsonAdapter.kt:6: Warning: Contribute this binding to the Dagger graph using an Anvil annotation [MissingContributesBindingAnnotation]
                class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                      ~~~~~~~~~~~~~
                0 errors, 1 warnings
            """
            )
            .expectWarningCount(1)
            .expectFixDiffs(
                """
                    Autofix for src/JsonAdapter.kt line 6: Add @ContributesBinding annotation:
                    @@ -6 +6
                    + @com.squareup.anvil.annotations.ContributesBinding
                    Autofix for src/JsonAdapter.kt line 6: Add @ContributesMultibinding annotation:
                    @@ -6 +6
                    + @com.squareup.anvil.annotations.ContributesMultibinding
                """
            )
    }

    @Test
    fun `kotlin class with @ContributesBinding annotation with a multiple supers does not show a warning`(
        @TestParameter(value = [CONTRIBUTES_BINDING, CONTRIBUTES_MULTI_BINDING]) annotation: String
    ) {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import $annotation

                    interface JsonAdapter<T>
                    interface CustomAdapter

                    @${annotation.substringAfterLast(".")}
                    class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin class with a super not using dagger without @ContributesBinding annotation does not show a warning`() {
        TestLintTask.lint()
            .files(
                TestFiles.kotlin(
                        """

                    interface Authenticator

                    class AuthenticatorImpl: Authenticator
                    """
                    )
                    .indented()
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java class with super using dagger without @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.java(
                        """
                    import javax.inject.Inject;

                    interface Authenticator {}

                    class AuthenticatorImpl implements Authenticator {

                        @Inject
                        public AuthenticatorImpl() {}
                    }
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `java class without super using dagger and no @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                javaxAnnotations,
                TestFiles.java(
                        """
                    import javax.inject.Inject;

                    class AuthenticatorImpl {

                    @Inject
                    public AuthenticatorImpl() {}
                    }
                    """
                    )
                    .indented(),
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
