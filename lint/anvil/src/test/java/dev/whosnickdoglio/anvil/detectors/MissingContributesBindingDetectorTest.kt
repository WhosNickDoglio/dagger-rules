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
import org.junit.Test

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
                    src/Authenticator.kt:5: Warning: hello [MissingContributesBindingAnnotation]
                    class AuthenticatorImpl @Inject constructor(): Authenticator
                          ~~~~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(1)
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
    fun `kotlin class with super using dagger with @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.kotlin(
                        """
                    import javax.inject.Inject
                    import com.squareup.anvil.annotations.ContributesBinding

                    interface Authenticator

                    @ContributesBinding
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
    fun `java class with super using dagger without @ContributesBinding annotation shows no warning`() {
        TestLintTask.lint()
            .files(
                anvilAnnotations,
                injectAnnotation,
                TestFiles.java(
                        """
                    import javax.inject.Inject;

                    interface Authenticator {}

                    class AuthenticatorImpl extends Authenticator {

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
