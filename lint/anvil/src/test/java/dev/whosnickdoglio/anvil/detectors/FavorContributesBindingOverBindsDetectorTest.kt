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

class FavorContributesBindingOverBindsDetectorTest {

    @Test
    fun `kotlin @Binds method should trigger warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds

                interface MyThing
                class MyThingImpl: MyThing

                @Module
                interface MyModule {

                @Binds
                fun provideMyThing(impl: MyThingImpl): MyThing

                }
            """
                    )
                    .indented()
            )
            .issues(FavorContributesBindingOverBindsDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:11: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                    fun provideMyThing(impl: MyThingImpl): MyThing
                        ~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `kotlin companion object @Binds method should trigger warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.kotlin(
                        """
                import dagger.Module
                import dagger.Binds

                interface MyThing
                class MyThingImpl: MyThing

                @Module
                interface MyModule {

                    @Binds
                    fun provideMyThing(impl: MyThingImpl): MyThing

                    companion object {
                        @Provides fun provideSomething(): String = "Hello world"
                    }

                }
            """
                    )
                    .indented()
            )
            .issues(FavorContributesBindingOverBindsDetector.ISSUE)
            .run()
            .expect(
                """
                    src/MyThing.kt:11: Warning: You can use @ContributesBinding over @Binds [ContributesBindingOverBinds]
                        fun provideMyThing(impl: MyThingImpl): MyThing
                            ~~~~~~~~~~~~~~
                    0 errors, 1 warnings
                """
                    .trimIndent()
            )
            .expectWarningCount(1)
    }

    @Test
    fun `java @Binds method should not trigger warning`() {
        TestLintTask.lint()
            .files(
                daggerAnnotations,
                TestFiles.java(
                        """
                import dagger.Module;
                import dagger.Binds;

                interface MyThing {}
                class MyThingImpl extends MyThing {}

                @Module
                interface MyModule {

                @Binds
                MyThing provideMyThing(MyThingImpl impl);
                }
            """
                    )
                    .indented()
            )
            .issues(FavorContributesBindingOverBindsDetector.ISSUE)
            .run()
            .expectClean()
            .expectWarningCount(0)
    }
}
