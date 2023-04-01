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
package dev.whosnickdoglio.dagger.detectors

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import org.junit.Test

class BindsWithCorrectReturnTypeDetectorTest {

    @Test
    fun `java @Binds method with a parameter that is a subclass of the return type does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    interface PizzaMaker {}
                    class PizzaMakerImpl extends PizzaMaker {}

                    @Module
                    interface MyModule {

                        @Binds PizzaMaker bindsPizzaMaker(PizzaMakerImpl pizzaMaker);
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `java @Binds method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                    import dagger.Module;
                    import dagger.Binds;

                    interface PizzaMaker {}
                    class NotPizzaMaker {}

                    @Module
                    interface MyModule {

                        @Binds PizzaMaker bindsPizzaMaker(PizzaMakerImpl pizzaMaker);
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.java:10: Error: @Binds method parameters need to be a subclass of the return type.
                    Make sure you're passing the correct parameter or the intended subclass is implementing
                    the return type interface. [BindsWithCorrectReturnType]
                        @Binds PizzaMaker bindsPizzaMaker(PizzaMakerImpl pizzaMaker);
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Binds method with a parameter that is a subclass of the return type does not trigger error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    interface PizzaMaker
                    class PizzaMakerImpl: PizzaMaker

                    @Module
                    interface MyModule {

                        @Binds fun bindsPizzaMaker(impl: PizzaMakerImpl): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    interface PizzaMaker
                    class NotPizzaMaker

                    @Module
                    interface MyModule {

                        @Binds fun bindsPizzaMaker(impl: NotPizzaMaker): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.kt:10: Error: @Binds method parameters need to be a subclass of the return type.
                    Make sure you're passing the correct parameter or the intended subclass is implementing
                    the return type interface. [BindsWithCorrectReturnType]
                        @Binds fun bindsPizzaMaker(impl: NotPizzaMaker): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }

    @Test
    fun `kotlin @Binds extension method with a parameter that is a subclass of the return type no error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    interface PizzaMaker
                    class PizzaMakerImpl: PizzaMaker

                    @Module
                    interface MyModule {

                        @Binds fun PizzaMakerImpl.bindPizzaMaker(): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expectClean()
            .expectErrorCount(0)
    }

    @Test
    fun `kotlin @Binds extension method with a parameter that is not a subclass of the return type triggers error`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                    import dagger.Module
                    import dagger.Binds

                    interface PizzaMaker
                    class NotPizzaMaker

                    @Module
                    interface MyModule {

                        @Binds fun NotPizzaMaker.bindPizzaMaker(): PizzaMaker
                    }
                """
                    )
                    .indented()
            )
            .issues(BindsWithCorrectReturnTypeDetector.ISSUE)
            .run()
            .expect(
                """
                    src/PizzaMaker.kt:10: Error: @Binds method parameters need to be a subclass of the return type.
                    Make sure you're passing the correct parameter or the intended subclass is implementing
                    the return type interface. [BindsWithCorrectReturnType]
                        @Binds fun NotPizzaMaker.bindPizzaMaker(): PizzaMaker
                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 errors, 0 warnings
                """
                    .trimIndent()
            )
            .expectErrorCount(1)
    }
}
