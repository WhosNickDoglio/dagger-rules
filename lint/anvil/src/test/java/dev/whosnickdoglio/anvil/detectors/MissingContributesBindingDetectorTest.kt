/*
 * MIT License
 *
 * Copyright (c) 2023 Nicholas Doglio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.whosnickdoglio.anvil.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import dev.whosnickdoglio.anvil.CONTRIBUTES_BINDING
import dev.whosnickdoglio.anvil.CONTRIBUTES_MULTI_BINDING
import dev.whosnickdoglio.stubs.injectAnnotation
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
class MissingContributesBindingDetectorTest {

    @Test
    fun `kotlin class with super using dagger is missing @ContributesBinding annotation shows warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface Authenticator

                    class AuthenticatorImpl @Inject constructor(): Authenticator
                    """
                    )
                    .indented()
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
                    .trimIndent()
            )
            .expectWarningCount(1)
            .expectFixDiffs(
                """
                Fix for src/Authenticator.kt line 5: Add @ContributesBinding annotation:
                @@ -5 +5
                - class AuthenticatorImpl @Inject constructor(): Authenticator
                @@ -6 +5
                + class @com.squareup.anvil.annotations.ContributesBinding
                + AuthenticatorImpl @Inject constructor(): Authenticator
                Fix for src/Authenticator.kt line 5: Add @ContributesMultibinding annotation:
                @@ -5 +5
                - class AuthenticatorImpl @Inject constructor(): Authenticator
                @@ -6 +5
                + class @com.squareup.anvil.annotations.ContributesMultibinding
                + AuthenticatorImpl @Inject constructor(): Authenticator
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin class without super using dagger and no @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    class AuthenticatorImpl @Inject constructor()
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
    fun `kotlin class with super using dagger with @ContributesBinding annotation shows no warning`(
        @TestParameter(value = [CONTRIBUTES_BINDING, CONTRIBUTES_MULTI_BINDING]) annotation: String
    ) {
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
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }

    @Test
    fun `kotlin class without @ContributesBinding annotation but only super takes generics shows no warning`() {
        TestLintTask.lint()
            .files(
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface JsonAdapter<T>

                    class MyJsonAdapter @Inject constructor(): JsonAdapter<String>
                    """
                    )
                    .indented()
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
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject

                    interface JsonAdapter<T>
                    interface CustomAdapter

                    class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                    """
                    )
                    .indented()
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
                    .trimIndent()
            )
            .expectWarningCount(1)
            .expectFixDiffs(
                """
                Fix for src/JsonAdapter.kt line 6: Add @ContributesBinding annotation:
                @@ -6 +6
                - class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                @@ -7 +6
                + class @com.squareup.anvil.annotations.ContributesBinding
                + MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                Fix for src/JsonAdapter.kt line 6: Add @ContributesMultibinding annotation:
                @@ -6 +6
                - class MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
                @@ -7 +6
                + class @com.squareup.anvil.annotations.ContributesMultibinding
                + MyJsonAdapter @Inject constructor(): JsonAdapter<String>, CustomAdapter
            """
                    .trimIndent()
            )
    }

    @Test
    fun `kotlin class with @ContributesBinding annotation with a multiple supers does not show a warning`(
        @TestParameter(value = [CONTRIBUTES_BINDING, CONTRIBUTES_MULTI_BINDING]) annotation: String
    ) {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
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
                    .indented()
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
                injectAnnotation,
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
                    .indented()
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
                injectAnnotation,
                TestFiles.java(
                        """
                    import javax.inject.Inject;

                    class AuthenticatorImpl {

                    @Inject
                    public AuthenticatorImpl() {}
                    }
                    """
                    )
                    .indented()
            )
            .issues(MissingContributesBindingDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }
}
